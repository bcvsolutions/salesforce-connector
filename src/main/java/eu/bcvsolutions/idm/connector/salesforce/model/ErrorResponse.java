package eu.bcvsolutions.idm.connector.salesforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class ErrorResponse {

	@JsonProperty("errorCode")
	private String error;

	@JsonProperty("message")
	private String message;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
