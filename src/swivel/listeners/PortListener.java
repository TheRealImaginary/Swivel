package swivel.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import swivel.view.ClientGUI;

public class PortListener implements ActionListener {

	private ClientGUI clientGUI;

	public PortListener(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		clientGUI.prepareIPListener(Integer.parseInt(e.getActionCommand()));
		clientGUI.clearUserText();
		clientGUI.showMessage("Please Enter a host!");
	}
}