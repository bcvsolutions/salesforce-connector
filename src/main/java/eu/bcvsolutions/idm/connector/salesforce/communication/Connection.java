package eu.bcvsolutions.idm.connector.salesforce.communication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.bcvsolutions.idm.connector.salesforce.model.AuthErrorResponse;
import eu.bcvsolutions.idm.connector.salesforce.operations.Authorization;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

/**
 * @author Roman Kucera
 */
public class Connection {

	private static final Log LOG = Log.getLog(Connection.class);

	/**
	 * Wrapped method for GET call to end system
	 *
	 * @param url
	 * @param authorization
	 * @return
	 */
	public HttpResponse<JsonNode> get(String url, Authorization authorization) {
		try {
			return Unirest.get(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization.getAuthorizationResponse().getAccessToken())
					.asJson();
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for POST call to end system
	 *
	 * @param url
	 * @param body
	 * @return
	 */
	public HttpResponse<JsonNode> postAuth(String url, Object body) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> formData = mapper.convertValue(body, new TypeReference<HashMap<String, Object>>() {
			});

			return Unirest.post(url)
					.fields(formData)
					.asJson();
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for POST call to end system
	 *
	 * @param url
	 * @param body
	 * @return
	 */
	public HttpResponse<JsonNode> post(String url, Map<String, Object> body, Authorization authorization) {
		try {
			return Unirest.post(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization.getAuthorizationResponse().getAccessToken())
					.header(HttpHeaders.CONTENT_TYPE, "application/json")
					.body(body)
					.asJson();
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	/**
	 * Wrapped method for PATCH call to end system
	 *
	 * @param url
	 * @param body
	 * @return
	 */
	public HttpResponse<JsonNode> patch(String url, Map<String, Object> body, Authorization authorization) {
		try {
			return Unirest.patch(url)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + authorization.getAuthorizationResponse().getAccessToken())
					.header(HttpHeaders.CONTENT_TYPE, "application/json")
					.body(body)
					.asJson();
		} catch (UnirestException e) {
			throw new ConnectionFailedException("Connection failed", e);
		}
	}

	public ConnectorException handleAuthError(HttpResponse<JsonNode> response, String operation, ConnectionFailedException e) {
		if (response != null) {
			try {
				ObjectMapper jsonObjectMapper = new ObjectMapper();

				AuthErrorResponse error = jsonObjectMapper.readValue(response.getBody().toString(), AuthErrorResponse.class);
				LOG.error("Operation {0} failed, error: {1}, description: {2}", operation, error.getError(), error.getErrorDescription());
				return new ConnectorException("Operation " + operation + " failed, error: " + error.getError() + ", description: " + error.getErrorDescription());
			} catch (IOException ex) {
				LOG.error("Can not parse error response for operation {0} " + ex, operation);
				return new ConnectorException("Can not parse error response for operation " + operation);
			}
		} else {
			LOG.error("Response is null can't parse error message for operation {0}", operation);
			return new ConnectorException(e);
		}
	}

	public String handleError(HttpResponse<JsonNode> response) {
		if (response.getParsingError().isPresent()) {
			return response.getParsingError().get().getOriginalBody();
		}
		return "";
	}
}
