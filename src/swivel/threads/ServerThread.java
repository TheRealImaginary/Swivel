package swivel.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.model.Server;

public class ServerThread extends Thread {

	private Socket client;
	private ObjectInputStream fromClient;
	private ObjectOutputStream toClient;
	private Server server;
	private String username;
	private int ID;

	public ServerThread(Socket client, Server server, int ID) {
		this.client = client;
		this.server = server;
		this.ID = ID;
		try {
			toClient = new ObjectOutputStream(client.getOutputStream());
			fromClient = new ObjectInputStream(client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Accepted Connection @" + client.getLocalSocketAddress());

		try {
			sendToClient(new Message("Server", "Client", "Welcome to RAD Chat Services", MessageType.SERVER_RESPONSE));
			sendToClient(
					new Message("Server", "Client", "Please Enter A Unique Username", MessageType.SERVER_RESPONSE));

			while (true) {
				System.err.println(server.getClients().length);
				Message msg = (Message) fromClient.readObject();
				if (msg.getMessageType() == MessageType.WHOS_IN) {
					// sendToClient(server.getClients());
					server.requestMembers(msg);
				} else if (msg.getMessageType() == MessageType.LOG_IN) {
					username = msg.getMessage();
					server.loginRequest(username, ID);
				} else if (msg.getMessageType() == MessageType.LOG_OUT) {
					if (!msg.getSender().isEmpty()) {
						server.logOut(msg.getSender());
						// server.broadCast(new Message("Server", "All",
						// username + " has left.", MessageType.LOG_OUT));
						sendToClient(new Message("Server", msg.getSender(), "", MessageType.LOG_OUT));
					} else
						sendToClient(new Message("Server", "", "", MessageType.LOG_OUT));
					server.removeThread(this);
					break;
				} else if (msg.getMessageType() == MessageType.MESSAGE) {
					server.route(msg);
				}
			}
			System.out.println("Job is done!");
			client.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void sendToClient(Object msg) {
		try {
			toClient.writeObject(msg);
			toClient.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToClient(Message msg) {
		try {
			toClient.writeObject(msg);
			toClient.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

	public String getUsername() {
		return username;
	}
}
