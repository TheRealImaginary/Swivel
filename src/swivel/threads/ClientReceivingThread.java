package swivel.threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.view.ClientGUI;

public class ClientReceivingThread extends Thread {

	private ClientGUI clientGUI;
	private ObjectInputStream fromServer;

	public ClientReceivingThread(ClientGUI clientGUI, ObjectInputStream fromServer) {
		this.clientGUI = clientGUI;
		this.fromServer = fromServer;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Object o = fromServer.readObject();
				if (o instanceof Message) {
					Message msg = (Message) o;
					System.err.println(msg + " " + Arrays.toString(msg.getMembers()));
					if (msg.getMessage() != null && !msg.getMessage().isEmpty())
						clientGUI.showMessage(msg.toString());
					if (msg.getMessageType() == MessageType.SERVER_RESPONSE) {
						if (msg.getMessage().equalsIgnoreCase("Accepted")) {
							clientGUI.setUserName(msg.getReceiver());
							clientGUI.begin();
						} else if (msg.getMessage().equalsIgnoreCase("Rejected"))
							clientGUI.showMessage("Try Another Username");
					} else if (msg.getMessageType() == MessageType.LOG_OUT
							|| msg.getMessageType() == MessageType.LOG_IN) {
						if (msg.getReceiver().equalsIgnoreCase("All"))
							clientGUI.getMemberList();
						else
							break;
					} else if (msg.getMessageType() == MessageType.WHOS_IN) {
						clientGUI.updateMemberList(msg.getMembers());
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		clientGUI.CloseConnection();
	}
}
