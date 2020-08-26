package eu.bcvsolutions.idm.connector.salesforce.operations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.bcvsolutions.idm.connector.salesforce.SalesforceConfiguration;
import eu.bcvsolutions.idm.connector.salesforce.communication.Connection;
import eu.bcvsolutions.idm.connector.salesforce.model.CreateResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

/**
 * @author Roman Kucera
 */
public class CreateOperation {

	private final String createUser = "/services/data/v49.0/sobjects/User";

	private SalesforceConfiguration configuration;
	private Connection connection;
	private Authorization authorization;

	public CreateOperation(SalesforceConfiguration configuration, Connection connection, Authorization authorization) {
		this.configuration = configuration;
		this.connection = connection;
		this.authorization = authorization;
	}

	public CreateResponse createUser(Set<Attribute> user) {
		Map<String, Object> userBody = new HashMap<>();
		user.forEach(attribute -> {
			if (attribute.getValue() == null || attribute.getValue().isEmpty()) {
				userBody.put(attribute.getName(), "");
			} else if (attribute.getValue().size() == 1) {
				userBody.put(attribute.getName(), attribute.getValue().get(0));
			}
			// TODO multivalued?
		});

		ObjectMapper jsonObjectMapper = new ObjectMapper();
		HttpResponse<JsonNode> response;
		try {
			if (!authorization.isAlive()) {
				authorization.authorize();
			}
			response = connection.post(configuration.getUrl() + createUser, userBody, authorization);
			if (response.getStatus() == HttpStatus.SC_OK || response.getStatus() == HttpStatus.SC_CREATED) {
				return jsonObjectMapper.readValue(response.getBody().toString(), CreateResponse.class);
			}
			throw new ConnectionFailedException("Can't connect to system, return code " + response.getStatus() + " body: " + response.getBody());
		} catch (JsonProcessingException e) {
			throw  new ConnectorException("Can't parse json", e);
		}
	}
}
