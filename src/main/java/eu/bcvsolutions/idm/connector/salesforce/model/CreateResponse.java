package eu.bcvsolutions.idm.connector.salesforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class CreateResponse {

	@JsonProperty("id")
	private String id;

	@JsonProperty("errors")
	private List<String> errors;

	@JsonProperty("success")
	private boolean success;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
