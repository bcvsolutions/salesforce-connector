package eu.bcvsolutions.idm.connector.salesforce;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.bcvsolutions.idm.connector.salesforce.communication.Connection;
import eu.bcvsolutions.idm.connector.salesforce.model.CreateResponse;
import eu.bcvsolutions.idm.connector.salesforce.model.UserResponse;
import eu.bcvsolutions.idm.connector.salesforce.operations.Authorization;
import eu.bcvsolutions.idm.connector.salesforce.operations.CreateOperation;
import eu.bcvsolutions.idm.connector.salesforce.operations.SearchOperation;
import eu.bcvsolutions.idm.connector.salesforce.operations.UpdateOperation;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;

/**
 * Main class of connector for Sales Force
 *
 * @author Roman Kucera
 */
@ConnectorClass(configurationClass = SalesforceConfiguration.class, displayNameKey = "salesforce.display")
public class SalesforceConnector implements Connector, CreateOp, UpdateOp, DeleteOp,
		SchemaOp, TestOp, SearchOp<String>, PoolableConnector {

	private static final Log LOG = Log.getLog(SalesforceConnector.class);

	private SalesforceConfiguration configuration;
	private Connection connection;
	private Authorization authorization;

	@Override
	public SalesforceConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void init(final Configuration configuration) {
		this.configuration = (SalesforceConfiguration) configuration;
		this.connection = new Connection();
		this.authorization = new Authorization(this.configuration, this.connection);

		Unirest.config()
				.verifySsl(false)
				.setObjectMapper(new ObjectMapper() {
					final com.fasterxml.jackson.databind.ObjectMapper mapper
							= new com.fasterxml.jackson.databind.ObjectMapper();

					public String writeValue(Object value) {
						try {
							return mapper.writeValueAsString(value);
						} catch (JsonProcessingException e) {
							LOG.error("Error during writing value {0}", e.getMessage());
							return null;
						}
					}

					public <T> T readValue(String value, Class<T> valueType) {
						try {
							return mapper.readValue(value, valueType);
						} catch (IOException e) {
							LOG.error("Error during reading value {0}", e.getMessage());
							return null;
						}
					}
				});

		LOG.ok("Connector {0} successfully inited", getClass().getName());
	}

	@Override
	public void dispose() {
		Unirest.shutDown();
	}

	@Override
	public Uid create(
			final ObjectClass objectClass,
			final Set<Attribute> createAttributes,
			final OperationOptions options) {

		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			CreateOperation createOperation = new CreateOperation(configuration, connection, authorization);
			CreateResponse user = createOperation.createUser(createAttributes);
			if (user.isSuccess()) {
				return new Uid(user.getId());
			}
			throw new ConnectorException("Error during creation" + user.getErrors());
		} else {
			throw new ConnectorException("Only __ACCOUNT__ object is supported");
		}
	}

	@Override
	public Uid update(
			final ObjectClass objectClass,
			final Uid uid,
			final Set<Attribute> replaceAttributes,
			final OperationOptions options) {

		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			UpdateOperation updateOperation = new UpdateOperation(configuration, connection, authorization);
			updateOperation.updateUser(replaceAttributes, uid.getUidValue());

			return uid;
		} else {
			throw new ConnectorException("Only __ACCOUNT__ object is supported");
		}
	}

	@Override
	public void delete(
			final ObjectClass objectClass,
			final Uid uid,
			final OperationOptions options) {
		throw new ConnectorException("Delete not supported");
	}

	@Override
	public Schema schema() {
		// Schema for users
		ObjectClassInfoBuilder accountObjectClassBuilder = new ObjectClassInfoBuilder();
		accountObjectClassBuilder.setType(ObjectClass.ACCOUNT_NAME);

		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Id", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Username", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("LastName", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("FirstName", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Name", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("CompanyName", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Division", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Department", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Title", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Street", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("City", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("State", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("PostalCode", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Country", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Latitude", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Longitude", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Address", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Email", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("SenderEmail", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("SenderName", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Signature", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Phone", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Fax", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("MobilePhone", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("Alias", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("CommunityNickname", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("FederationIdentifier", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("EmailEncodingKey", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("LanguageLocaleKey", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("LocaleSidKey", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("ProfileId", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("TimeZoneSidKey", String.class));
		accountObjectClassBuilder.addAttributeInfo(AttributeInfoBuilder.build("IsActive", Boolean.class));

		SchemaBuilder schemaBuilder = new SchemaBuilder(SalesforceConnector.class);
		schemaBuilder.defineObjectClass(accountObjectClassBuilder.build());
		return schemaBuilder.build();
	}

	@Override
	public void test() {
		authorization.authorize();
	}

	@Override
	public FilterTranslator<String> createFilterTranslator(
			final ObjectClass objectClass,
			final OperationOptions options) {

		if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
			return new AbstractFilterTranslator<String>() {
				@Override
				protected String createEqualsExpression(EqualsFilter filter, boolean not) {
					if (not) {
						throw new UnsupportedOperationException("This type of equals expression is not allow for now.");
					}

					Attribute attr = filter.getAttribute();

					if (attr == null || !attr.is(Uid.NAME)) {
						throw new IllegalArgumentException("Attribute is null or not UID attribute.");
					}

					return ((Uid) attr).getUidValue();
				}
			};
		}
		return null;
	}

	@Override
	public void executeQuery(
			final ObjectClass objectClass,
			final String query,
			final ResultsHandler handler,
			final OperationOptions options) {
		SearchOperation searchOperation = new SearchOperation(configuration, connection, authorization);
		if (query != null) {
			Map<String, Object> user = searchOperation.getUser(query);
			if (!user.isEmpty()) {
				ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
				builder.setObjectClass(objectClass);
				user.forEach((key, value) -> {
					if (key.equals("Id")) {
						builder.setUid(String.valueOf(value));
					}
					if (key.equals("Username")) {
						builder.setName(String.valueOf(value));
					}
					builder.addAttribute(AttributeBuilder.build(key, value));
				});
				handler.handle(builder.build());
			}
		} else {
			UserResponse users = searchOperation.getUsers();
			users.getRecords().forEach(userRecord -> {
				ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
				builder.setUid(userRecord.getId());
				builder.setName(userRecord.getUsername());
				builder.setObjectClass(objectClass);
				handler.handle(builder.build());
			});
		}
	}

	@Override
	public void checkAlive() {
		if(!authorization.isAlive()) {
			authorization.authorize();
		}
	}
}
