package swivel.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import swivel.model.Message;
import swivel.model.MessageType;
import swivel.view.ClientGUI;

public class ClientWindowListener implements WindowListener {

	private ClientGUI clientGUI;

	public ClientWindowListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		System.err.println("ss");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		String s = clientGUI.getUserName();
		s = s == null ? "" : s;
		clientGUI.sendMessage(new Message(s, "Server", "", MessageType.LOG_OUT));
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
