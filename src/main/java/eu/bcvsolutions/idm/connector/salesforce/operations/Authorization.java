package eu.bcvsolutions.idm.connector.salesforce.operations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.http.HttpStatus;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.bcvsolutions.idm.connector.salesforce.SalesforceConfiguration;
import eu.bcvsolutions.idm.connector.salesforce.communication.Connection;
import eu.bcvsolutions.idm.connector.salesforce.communication.GuardedStringAccessor;
import eu.bcvsolutions.idm.connector.salesforce.model.AuthorizationRequest;
import eu.bcvsolutions.idm.connector.salesforce.model.AuthorizationResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

/**
 * @author Roman Kucera
 */
public class Authorization {

	private SalesforceConfiguration configuration;
	private Connection connection;
	private AuthorizationResponse authorizationResponse;

	public Authorization(SalesforceConfiguration configuration, Connection connection) {
		this.configuration = configuration;
		this.connection = connection;
	}

	public void authorize() {
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setUsername(configuration.getUsername());
		authorizationRequest.setPassword(getPassword(configuration.getPassword()) + getPassword(configuration.getToken()));
		authorizationRequest.setClientId(getPassword(configuration.getClientId()));
		authorizationRequest.setClientSecret(getPassword(configuration.getClientSecret()));
		authorizationRequest.setGrantType(configuration.getGrandType());

		ObjectMapper jsonObjectMapper = new ObjectMapper();
		HttpResponse<JsonNode> response = null;
		try {
			response = connection.postAuth(configuration.getUrl() + configuration.getAuthUrl(), authorizationRequest);
			if (response.getStatus() != HttpStatus.SC_OK) {
				throw new ConnectionFailedException("Can't connect to system, return code " + response.getStatus());
			}
			authorizationResponse = jsonObjectMapper.readValue(response.getBody().toString(), AuthorizationResponse.class);
		} catch (JsonProcessingException e) {
			throw  new ConnectorException("", e);
		} catch (ConnectionFailedException ex) {
			throw connection.handleAuthError(response, "auth", ex);
		}
	}

	public boolean isAlive() {
		if (authorizationResponse == null) {
			return false;
		}
		if (StringUtil.isBlank(authorizationResponse.getIssuedAt())) {
			return false;
		}
		Instant instant = Instant.ofEpochMilli(Long.parseLong(authorizationResponse.getIssuedAt()));
		LocalDateTime issuedAt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

		return !issuedAt.plusMinutes(configuration.getValidity()).isAfter(LocalDateTime.now());
	}

	/**
	 * Get password as plain string
	 *
	 * @param password
	 * @return
	 */
	private String getPassword(GuardedString password) {
		GuardedStringAccessor accessor = new GuardedStringAccessor();
		password.access(accessor);
		char[] result = accessor.getArray();
		return new String(result);
	}

	public AuthorizationResponse getAuthorizationResponse() {
		return authorizationResponse;
	}

	public void setAuthorizationResponse(AuthorizationResponse authorizationResponse) {
		this.authorizationResponse = authorizationResponse;
	}
}
