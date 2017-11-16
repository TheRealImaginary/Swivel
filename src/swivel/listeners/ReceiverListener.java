package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import swivel.view.ClientGUI;

public class ReceiverListener implements ActionListener {

	private ClientGUI clientGUI;

	public ReceiverListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientGUI.process((JButton) e.getSource());
	}

}
