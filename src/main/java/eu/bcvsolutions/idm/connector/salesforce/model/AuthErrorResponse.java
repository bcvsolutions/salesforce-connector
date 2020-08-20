package eu.bcvsolutions.idm.connector.salesforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class AuthErrorResponse {

	@JsonProperty("error")
	private String error;

	@JsonProperty("error_description")
	private String errorDescription;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
