package com.parse.app.model;

public class Message {

	String message;
	boolean isMine;
	boolean isStatusMessage;

	public Message(String message, boolean isMine) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
	}

	public Message(boolean status, String message) {
		super();
		this.message = message;
		this.isMine = false;
		this.isStatusMessage = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public boolean isStatusMessage() {
		return isStatusMessage;
	}
	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	
}
