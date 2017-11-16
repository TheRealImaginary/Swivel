package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import swivel.view.ClientGUI;

public class RefreshListener implements ActionListener {

	private ClientGUI clientGUI;

	public RefreshListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientGUI.getMemberList();
	}

}
