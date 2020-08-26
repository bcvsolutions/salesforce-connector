package eu.bcvsolutions.idm.connector.salesforce.operations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.identityconnectors.framework.common.exceptions.ConnectionFailedException;
import org.identityconnectors.framework.common.objects.Attribute;

import eu.bcvsolutions.idm.connector.salesforce.SalesforceConfiguration;
import eu.bcvsolutions.idm.connector.salesforce.communication.Connection;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

/**
 * @author Roman Kucera
 */
public class UpdateOperation {

	private final String updateUser = "/services/data/v49.0/sobjects/User/";

	private SalesforceConfiguration configuration;
	private Connection connection;
	private Authorization authorization;

	public UpdateOperation(SalesforceConfiguration configuration, Connection connection, Authorization authorization) {
		this.configuration = configuration;
		this.connection = connection;
		this.authorization = authorization;
	}

	public void updateUser(Set<Attribute> user, String id) {
		Map<String, Object> userBody = new HashMap<>();
		user.forEach(attribute -> {
			if (attribute.getValue() == null || attribute.getValue().isEmpty()) {
				userBody.put(attribute.getName(), "");
			} else if (attribute.getValue().size() == 1) {
				userBody.put(attribute.getName(), attribute.getValue().get(0));
			}
			// TODO multivalued?
		});

		HttpResponse<JsonNode> response;
		if (!authorization.isAlive()) {
			authorization.authorize();
		}
		response = connection.patch(configuration.getUrl() + updateUser + id, userBody, authorization);
		if (response.getStatus() != HttpStatus.SC_NO_CONTENT) {
			throw new ConnectionFailedException("Can't connect to system, return code " + response.getStatus() + " body: " + response.getBody());
		}
	}
}
