package swivel.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.TreeSet;

import swivel.threads.CentralServerThread;

public class CentralServer {

	public static final int PORT = 7000;
	private ServerSocket serverSocket;
	private TreeSet<String> allUsers;
	private ArrayList<CentralServerThread> serverThreads;

	public CentralServer() {
		allUsers = new TreeSet<>();
		serverThreads = new ArrayList<>();
		start();
	}

	public void start() {
		System.out.println("Central Server Listening at port : " + PORT);

		try {
			serverSocket = new ServerSocket(PORT);
			while (true) {
				CentralServerThread ct = new CentralServerThread(serverSocket.accept(), this);
				serverThreads.add(ct);
				ct.start();
			}
		} catch (IOException ioe) {
			System.err.println("Error When Starting Central Server!");
		}
	}

	public synchronized void route(Message msg) {
		for (CentralServerThread cst : serverThreads) {
			cst.sendToServer(msg);
		}
	}

	public synchronized boolean checkUsername(String username) {
		if (allUsers.contains(username))
			return false;
		allUsers.add(username);
		return true;
	}

	public synchronized void logOut(String username) {
		allUsers.remove(username);
		route(new Message("Server", "All", username + " has logged out", MessageType.LOG_OUT));
	}

	public synchronized String[] getAllUsers() {
		return allUsers.toArray(new String[0]);
	}

	public static void main(String[] args) {
		CentralServer cs = new CentralServer();
	}
}
