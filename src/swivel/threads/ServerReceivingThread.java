package swivel.threads;

import java.io.IOException;
import java.io.ObjectInputStream;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.model.Server;

public class ServerReceivingThread extends Thread {

	private Server server;
	private ObjectInputStream fromCentral;

	public ServerReceivingThread(Server server, ObjectInputStream fromCentral) {
		this.server = server;
		this.fromCentral = fromCentral;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Object o = fromCentral.readObject();
				if (o instanceof Message) {
					Message msg = (Message) o;
					if (msg.getMessageType() == MessageType.WHOS_IN || msg.getMessageType() == MessageType.MESSAGE) {
						server.route(msg);
					} else if (msg.getMessageType() == MessageType.SERVER_RESPONSE) {
						server.loginResponse(msg);
					} else if (msg.getMessageType() == MessageType.LOG_OUT
							|| msg.getMessageType() == MessageType.LOG_IN) {
						server.broadCast(msg);
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
