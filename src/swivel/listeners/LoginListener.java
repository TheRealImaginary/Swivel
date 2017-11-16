package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.view.ClientGUI;

public class LoginListener implements ActionListener {

	private ClientGUI clientGUI;

	public LoginListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().isEmpty()) {
			// clientGUI.setUserName(e.getActionCommand());
			clientGUI.sendMessage(new Message("Client", "Server", e.getActionCommand(), MessageType.LOG_IN));
			clientGUI.clearUserText();
		}
	}

}
