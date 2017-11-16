package swivel.view;

import java.awt.BorderLayout;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import swivel.listeners.ClientWindowListener;
import swivel.listeners.IPListener;
import swivel.listeners.LoginListener;
import swivel.listeners.MessageListener;
import swivel.listeners.PortListener;
import swivel.listeners.ReceiverListener;
import swivel.listeners.RefreshListener;
import swivel.model.Message;
import swivel.model.MessageType;
import swivel.model.Server;
import swivel.threads.ClientReceivingThread;

public class ClientGUI extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	private Socket connection;
	private ClientReceivingThread crt;
	private String username;
	private LoginListener loginListener;
	private IPListener IPListener;
	private JButton refresh;
	private JPanel panel;
	private ArrayList<JButton> members;
	private JButton receiver;
	private PortListener portListener;
	private int port;

	public ClientGUI() {
		super("The Client");
		userText = new JTextField();
		userText.setEditable(true);
		userText.addActionListener(portListener = new PortListener(this));
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(550, 500);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		refresh = new JButton("Refresh Members");

		panel.add(refresh);

		add(panel, BorderLayout.EAST);

		addWindowListener(new ClientWindowListener(this));

		showMessage("Please Enter a port");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void prepareIPListener(int port) {
		this.port = port;
		userText.removeActionListener(portListener);
		userText.addActionListener(IPListener = new IPListener(this));
	}

	public void start(String host) {
		try {
			connectToServer(host);
			setupStreams();
			userText.removeActionListener(IPListener);
			userText.addActionListener(loginListener = new LoginListener(this));
			crt = new ClientReceivingThread(this, fromServer);
			crt.start();
		} catch (EOFException eof) {
			showMessage("client terminated the connection");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void begin() {
		userText.removeActionListener(loginListener);
		userText.addActionListener(new MessageListener(this));
		refresh.addActionListener(new RefreshListener(this));
	}

	public void getMemberList() {
		sendMessage(new Message(getUserName(), "Server", "", MessageType.WHOS_IN));
	}

	public void updateMemberList(String[] clients) {
		panel.removeAll();
		members = new ArrayList<>();
		for (String c : clients) {
			JButton b;
			if (c.equals(username))
				b = new JButton(c + " (You)");
			else
				b = new JButton(c);
			members.add(b);
			panel.add(b);
			b.addActionListener(new ReceiverListener(this));
		}
		panel.add(refresh);
		panel.revalidate();
		revalidate();
	}

	public void process(JButton b) {
		if (receiver != null) {
			receiver.setEnabled(true);
		}
		receiver = b;
		receiver.setEnabled(false);
	}

	public String getReceiverName() {
		System.err.println(receiver.getText());
		return receiver.getText();
	}

	private void connectToServer(String host) {
		showMessage("conncecting");
		try {
			connection = new Socket(host, port);
			showMessage("connected to:" + connection.getInetAddress().getHostName());
		} catch (IOException ioe) {
			System.err.println("Client cannot connect to server!");
		}
	}

	private void setupStreams() throws IOException {
		toServer = new ObjectOutputStream(connection.getOutputStream());
		fromServer = new ObjectInputStream(connection.getInputStream());
		showMessage("Streams Setup Succesful");
	}

	// close the streams and sockets
	public void CloseConnection() {
		showMessage("Closing connection ");
		try {
			toServer.close();
			fromServer.close();
			connection.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void clearUserText() {
		userText.setText("");
	}

	public void sendMessage(Message message) {
		try {
			toServer.writeObject(message);
			toServer.flush();
		} catch (IOException s) {
			chatWindow.append("something messed up the sending");
		}
	}

	public void showMessage(String m) {
		chatWindow.append(m + "\n");
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public static void main(String[] args) {
		ClientGUI client = new ClientGUI();
		// client.start("localhost");
	}
}
