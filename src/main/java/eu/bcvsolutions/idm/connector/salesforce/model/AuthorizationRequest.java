package eu.bcvsolutions.idm.connector.salesforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class AuthorizationRequest {

	@JsonProperty("grant_type")
	private String grantType;

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("client_secret")
	private String clientSecret;

	@JsonProperty("username")
	private String username;

	@JsonProperty("password")
	private String password;

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
