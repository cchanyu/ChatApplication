import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatApp extends JFrame implements KeyListener {

	// label, button, text-field initial creation
	public JTextArea textArea;
	public JTextField textfield3;
	public JButton send, close;
	public InetAddress IPdest, ip2;
	public int port, port2;
	public Socket socket;
	public String MyName = ChatUI.getMyName(), OtherPerson = ChatUI.getOtherName();

	public ChatApp(Socket connect, InetAddress ip, int port) {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			this.ip2 = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		this.port2 = 64000;

		// Jframe title displaying other person's ip and port
		setTitle(OtherPerson + " - Connected to: " + ip + ": " + port);
		setSize(900, 800);

		// Menu bar settings
		JMenuBar mb = new JMenuBar();
		JMenu m1 = new JMenu("Menu");
		JMenuItem m11 = new JMenuItem(new AbstractAction("Send") {
			public void actionPerformed(ActionEvent ee1) {
				send.doClick();
			}
		});
		JMenuItem m12 = new JMenuItem(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent ee2) {
				close.doClick();
			}
		});
		JMenu m2 = new JMenu("Quick Text");
		JMenuItem m21 = new JMenuItem(new AbstractAction("Hello!") {
			public void actionPerformed(ActionEvent ee3) {
				textfield3.setText("Hello!");
				send.doClick();
			}
		});
		JMenuItem m22 = new JMenuItem(new AbstractAction("Goodbye!") {
			public void actionPerformed(ActionEvent ee4) {
				textfield3.setText("Goodbye!");
				send.doClick();
			}
		});
		mb.add(m1);
		m1.add(m11);
		m1.add(m12);
		mb.add(m2);
		m2.add(m21);
		m2.add(m22);
		getContentPane().add(BorderLayout.NORTH, mb);

		// Jpanel settings
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel);

		// Text area settings
		textArea = new JTextArea();
		textArea.setEditable(false);
		panel.add(textArea);

		// Text field settings
		JPanel bottom = new JPanel(new BorderLayout());
		panel.add(bottom, BorderLayout.SOUTH);
		textfield3 = new JTextField();
		bottom.add(textfield3);

		// Send button settings
		JPanel button = new JPanel(new BorderLayout());
		bottom.add(button, BorderLayout.EAST);
		send = new JButton("Send");

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// if the text-field isn't empty, it sends the message and display on your
				// screen.
				if (!textfield3.getText().isEmpty()) {
					String message = textfield3.getText().toString().trim();
					InetAddress destination = null;
					textArea.append(MyName + " (" + ip2 + ": " + port2 + ") " + message + "\n");

					destination = ip;
					connect.send(message, destination, port);

					// this resets the text area
					textfield3.setText("");
				}

			}

		});
		button.add(send, BorderLayout.WEST);

		// Listens for Enter key press
		textfield3.addKeyListener(this);

		close = new JButton("Close");
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}

		});
		button.add(close);

		setVisible(true);
	}

	// Returns textArea
	public JTextArea getText() { return this.textArea; }
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyReleased(KeyEvent e) { }

	// It clicks send button whenever I press enter key
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			send.doClick();
		}
	}
}
