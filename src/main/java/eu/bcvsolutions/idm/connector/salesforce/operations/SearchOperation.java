package eu.bcvsolutions.idm.connector.salesforce.operations;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.bcvsolutions.idm.connector.salesforce.SalesforceConfiguration;
import eu.bcvsolutions.idm.connector.salesforce.communication.Connection;
import eu.bcvsolutions.idm.connector.salesforce.model.UserResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

/**
 * @author Roman Kucera
 */
public class SearchOperation {

	private static final Log LOG = Log.getLog(SearchOperation.class);

	private final String getAllUsersUrl = "/services/data/v49.0/query?q=SELECT+id,Username+FROM+User";
	private final String getUserDetail = "/services/data/v49.0/sobjects/User/";

	private SalesforceConfiguration configuration;
	private Connection connection;
	private Authorization authorization;

	public SearchOperation(SalesforceConfiguration configuration, Connection connection, Authorization authorization) {
		this.configuration = configuration;
		this.connection = connection;
		this.authorization = authorization;
	}

	public UserResponse getUsers() {
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		HttpResponse<JsonNode> response;
		try {
			// TODO check pagable?
			if (!authorization.isAlive()) {
				authorization.authorize();
			}
			response = connection.get(configuration.getUrl() + getAllUsersUrl, authorization);
			if (response.getStatus() == HttpStatus.SC_OK) {
				return jsonObjectMapper.readValue(response.getBody().toString(), UserResponse.class);
			}
			throw new ConnectionFailedException("Can't connect to system, return code " + response.getStatus() + " body: " + response.getBody()
					+ " other info: " + connection.handleError(response));
		} catch (JsonProcessingException e) {
			throw new ConnectorException("Can't parse json", e);
		}
	}

	public Map<String, Object> getUser(String id) {
		ObjectMapper jsonObjectMapper = new ObjectMapper();
		HttpResponse<JsonNode> response;
		try {
			if (!authorization.isAlive()) {
				authorization.authorize();
			}
			response = connection.get(configuration.getUrl() + getUserDetail + id, authorization);
			if (response.getStatus() == HttpStatus.SC_OK) {
				return jsonObjectMapper.readValue(response.getBody().toString(), new TypeReference<HashMap<String, Object>>() {});
			}
			if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
				LOG.info("User not found");
				return new HashMap<>();
			}
			throw new ConnectionFailedException("Can't connect to system, return code " + response.getStatus() + " body: " + response.getBody()
					+ " other info: " + connection.handleError(response));
		} catch (JsonProcessingException e) {
			throw new ConnectorException("Can't parse json", e);
		}
	}
}
