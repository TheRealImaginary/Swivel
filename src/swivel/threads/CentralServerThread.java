package swivel.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import swivel.model.CentralServer;
import swivel.model.Message;
import swivel.model.MessageType;

public class CentralServerThread extends Thread {

	private Socket server;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	private CentralServer central;

	public CentralServerThread(Socket server, CentralServer central) {
		this.server = server;
		this.central = central;
		try {
			toServer = new ObjectOutputStream(server.getOutputStream());
			fromServer = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToServer(Message msg) {
		try {
			toServer.writeObject(msg);
			toServer.flush();
		} catch (IOException ioe) {
			System.err.println("Error sending a message to server");
		}
	}

	@Override
	public void run() {
		System.out.println("Connected To Server");
		try {
			while (true) {
				Object o = fromServer.readObject();
				if (o instanceof Message) {
					Message msg = (Message) o;
					msg.decTTL();
					if (!msg.isAlive())
						continue;
					if (msg.getMessageType() == MessageType.LOG_IN) {
						if (msg.getReceiver().equalsIgnoreCase("All")) {
							central.route(msg);
							central.route(new Message("Central", "Server", central.getAllUsers(), MessageType.WHOS_IN));
						} else {
							boolean response = central.checkUsername(msg.getMessage());
							sendToServer(new Message("Central", msg.getSender(), response ? "Accepted" : "Rejected",
									MessageType.SERVER_RESPONSE));
						}
					} else if (msg.getMessageType() == MessageType.LOG_OUT) {
						central.logOut(msg.getMessage());
					} else if (msg.getMessageType() == MessageType.WHOS_IN) {
						sendToServer(
								new Message("Central", msg.getSender(), central.getAllUsers(), MessageType.WHOS_IN));
					} else if (msg.getMessageType() == MessageType.MESSAGE) {
						central.route(msg);
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
