package com.viettel.backend.websocket.core;


public class OutputMessage {
	
    private String senderId;
	private String type;
	private Object data;
	
	public OutputMessage(){
		this(null, null, null);
	}
	
	public OutputMessage(String senderId, String type, Object data) {
		super();
		this.senderId = senderId;
		this.type = type;
		this.data = data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
	
}
