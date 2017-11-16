package swivel.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import swivel.threads.ServerReceivingThread;
import swivel.threads.ServerThread;

public class Server {

	public static final int PORT = 6000;
	private ServerSocket clientSocket;
	private TreeSet<String> usernames;
	private ArrayList<ServerThread> clients;

	private Socket central;
	private ObjectOutputStream toCentral;
	private ObjectInputStream fromCentral;
	private ServerReceivingThread srt;

	private int clientsNum;
	private int port;

	public Server(int port) {
		usernames = new TreeSet<String>();
		clients = new ArrayList<>();
		this.port = port;
		try {
			central = new Socket("localhost", CentralServer.PORT);
			toCentral = new ObjectOutputStream(central.getOutputStream());
			fromCentral = new ObjectInputStream(central.getInputStream());
			srt = new ServerReceivingThread(this, fromCentral);
			srt.start();
			start();
		} catch (IOException ioe) {
			System.err.println("Error When Starting Server ");
		}
	}

	public void start() throws IOException {
		clientsNum = 0;

		System.out.println("Listening on port " + port);

		clientSocket = new ServerSocket(port);
		while (true) {
			ServerThread st = new ServerThread(clientSocket.accept(), this, ++clientsNum);
			clients.add(st);
			st.start();
		}
	}

	public void sendToCentral(Message msg) {
		try {
			toCentral.writeObject(msg);
			toCentral.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public synchronized void loginRequest(String username, int ID) {
		sendToCentral(new Message(Integer.toString(ID), "Central", username, MessageType.LOG_IN));
	}

	public void loginResponse(Message msg) {
		String username = null;
		for (ServerThread st : clients) {
			if (st.getID() == Integer.parseInt(msg.getReceiver())) {
				username = st.getUsername();
				st.sendToClient(new Message("Server", username, msg.getMessage(), msg.getMessageType()));
				break;
			}
		}
		if (msg.getMessage().equalsIgnoreCase("Accepted") && username != null) {
			usernames.add(username);
			sendToCentral(new Message("Server", "All", "", MessageType.LOG_IN));
			System.err.println(port + " " + usernames);
		}
	}

	public synchronized void logOut(String username) {
		usernames.remove(username);
		sendToCentral(new Message("Server", "Central", username, MessageType.LOG_OUT));
	}

	public synchronized void removeThread(ServerThread st) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i) == st) {
				clients.remove(i);
				break;
			}
	}

	public synchronized void broadCast(Message msg) {
		for (ServerThread st : clients)
			// if (!st.getUsername().equals(msg.getSender()))
			st.sendToClient(msg);
	}

	public synchronized void route(Message msg) {
		if (msg.getReceiver().equalsIgnoreCase("Server")) {
			broadCast(msg);
			return;
		}
		for (ServerThread st : clients)
			if (st.getUsername().equals(msg.getReceiver())) {
				st.sendToClient(msg);
				return;
			}
		sendToCentral(msg);
	}

	public synchronized void requestMembers(Message msg) {
		sendToCentral(msg);
	}

	// Returning a TreeSet had a weird bug that made clients not update
	// members after the first update was requested
	public synchronized String[] getClients() {
		return usernames.toArray(new String[0]);
	}

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please Enter a port number");
		Server server = new Server(sc.nextInt());
		// server.start();
	}
}
