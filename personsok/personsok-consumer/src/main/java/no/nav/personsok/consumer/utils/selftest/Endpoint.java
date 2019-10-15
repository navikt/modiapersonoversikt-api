package no.nav.personsok.consumer.utils.selftest;

import java.io.Serializable;

public class Endpoint implements Serializable{
	private String name;
	private Boolean statusOk;
	private String responseTime;
	private String code;
	private long elapsedTime;

	public Endpoint() {
	}

	public Endpoint(String code, String name, String responseTime, Boolean statusOk, long elapsedTime) {
		this.code = code;
		this.name = name;
		this.responseTime = responseTime;
		this.statusOk = statusOk;
		this.elapsedTime = elapsedTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public Boolean getStatusOk() {
		return statusOk;
	}

	public void setStatusOK(Boolean statusOk) {
		this.statusOk = statusOk;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
}
