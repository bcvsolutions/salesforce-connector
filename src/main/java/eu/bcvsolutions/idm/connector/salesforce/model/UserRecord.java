package eu.bcvsolutions.idm.connector.salesforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class UserRecord {

	@JsonProperty("attributes")
	private Attribute attributes;

	@JsonProperty("Id")
	private String id;

	@JsonProperty("Username")
	private String username;

	public Attribute getAttributes() {
		return attributes;
	}

	public void setAttributes(Attribute attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
