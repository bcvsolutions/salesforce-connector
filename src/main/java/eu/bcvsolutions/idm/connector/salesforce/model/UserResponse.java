package eu.bcvsolutions.idm.connector.salesforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roman Kucera
 */
public class UserResponse {

	@JsonProperty("totalSize")
	private int totalSize;

	@JsonProperty("done")
	private String done;

	@JsonProperty("records")
	private List<UserRecord> records;

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getDone() {
		return done;
	}

	public void setDone(String done) {
		this.done = done;
	}

	public List<UserRecord> getRecords() {
		return records;
	}

	public void setRecords(List<UserRecord> records) {
		this.records = records;
	}
}
