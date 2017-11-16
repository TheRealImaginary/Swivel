package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.view.ClientGUI;

public class MessageListener implements ActionListener {

	private ClientGUI clientGUI;

	public MessageListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().isEmpty()) {
			clientGUI.sendMessage(new Message(clientGUI.getUserName(), clientGUI.getReceiverName(),
					(String) e.getActionCommand(), MessageType.MESSAGE));
			clientGUI.showMessage(clientGUI.getUserName() + " : " + e.getActionCommand());
			clientGUI.clearUserText();
		}
	}

}
