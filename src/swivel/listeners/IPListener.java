package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import swivel.view.ClientGUI;

public class IPListener implements ActionListener {

	private ClientGUI clientGUI;

	public IPListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientGUI.start(e.getActionCommand());
		clientGUI.clearUserText();
	}
}
