package swivel.model;

import java.io.Serializable;

public class Message implements Serializable {

	private String sender;
	private String receiver;
	private String message;
	private String[] members;
	private MessageType messageType;
	private int TTL;

	public Message(String sender, String receiver, String message, MessageType messageType) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.messageType = messageType;
		TTL = 2;
	}

	public Message(String sender, String receiver, String[] members, MessageType messageType) {
		this.sender = sender;
		this.receiver = receiver;
		this.members = members;
		this.messageType = messageType;
		TTL = 2;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getMessage() {
		return message;
	}

	public String[] getMembers() {
		return members;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public int getTTL() {
		return TTL;
	}

	public void decTTL() {
		TTL--;
	}

	public boolean isAlive() {
		return TTL > 0;
	}

	@Override
	public String toString() {
		return sender + " : " + message;
	}
}
