import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.util.HashMap;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatUI implements ActionListener {

	// Connect Chat Creation
	public static int port = 64000, count;
	public static Socket socket = new Socket(port, Socket.SocketType.Broadcast);
	public static JLabel label, label1, label2, label3, label4, label5, label6;
	public static JTextField textfield, textfield2, textfield3;
	public static JButton connect, broadcast, exit, Submit;
	public static JFrame Start;
	public static Map<InetAddress, ChatApp> newChat = new HashMap<InetAddress, ChatApp>();

	// Broadcast Chat Creation
	public static String MyName = InputUI.getMyName();
	public static String OtherPerson = "Person", Receiver = "";
	public static String RequestedIP;
	public static String myIP2;
	public static InetAddress otherIP = null, myIP = socket.getMyAddress();
	public static DatagramSocket mySocket = null;
	public static Socket sendingInstance;
	public static Thread receiveThread;

	public ChatUI() {
		Start();
	}

	// GUI that takes other person's info
	public void Start() {
		Start = new JFrame();
		Start.getContentPane().setLayout(null);
		Start.getContentPane().setFont(new Font("Arial", Font.CENTER_BASELINE, 12));
		Start.getContentPane().setBackground(new Color(240, 240, 240));
		Start.setTitle("Welcome " + MyName + "!!");

		// Enter name label
		label = new JLabel("IP Address : ");
		label.setBounds(10, 10, 100, 100);
		label.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(label);

		label2 = new JLabel("Port Number : ");
		label2.setBounds(10, 50, 100, 100);
		label2.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(label2);

		label4 = new JLabel("Connect to : ");
		label4.setBounds(10, 90, 100, 100);
		label4.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(label4);

		// Label for debug
		label1 = new JLabel();
		label1.setText("My IP: " + socket.getMyAddress());
		myIP2 = myIP.toString();
		label1.setToolTipText(myIP2);
		label1.setBounds(10, 200, 300, 100);
		Start.add(label1);

		label3 = new JLabel();
		label3.setText("My Port: " + socket.getMyPortNumber());
		label3.setBounds(10, 220, 300, 100);
		Start.add(label3);

		label5 = new JLabel();
		label5.setBounds(10, 240, 300, 100);
		Start.add(label5);

		label6 = new JLabel();
		label6.setBounds(10, 260, 300, 100);
		Start.add(label6);

		// Text field to enter name
		textfield = new JTextField();
		textfield.setBounds(110, 45, 130, 30);
		Start.add(textfield);

		textfield2 = new JTextField();
		textfield2.setBounds(110, 85, 130, 30);
		Start.add(textfield2);

		textfield3 = new JTextField();
		textfield3.setBounds(110, 125, 130, 30);
		Start.add(textfield3);

		// Submit button
		connect = new JButton("Connect");
		connect.setBounds(10, 170, 80, 40);
		connect.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(connect);
		connect.addActionListener(this);

		broadcast = new JButton("Broadcast");
		broadcast.setBounds(110, 170, 80, 40);
		broadcast.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(broadcast);
		broadcast.addActionListener(this);

		exit = new JButton("Close");
		exit.setBounds(210, 170, 80, 40);
		exit.setFont(new Font("Arial", Font.BOLD, 12));
		Start.add(exit);
		exit.addActionListener(this);

		Start.setSize(300, 400);
		Start.setLayout(null);
		Start.setVisible(true);
		Start.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// IP isn't in the chat- it add. if not, pulls up current chat.
	public static void ChatWindow(InetAddress ip, int port, String message) {
		if (!(newChat.containsKey(ip))) {
			ChatApp newchat = new ChatApp(socket, ip, port);
			newChat.put(ip, newchat);
			newchat.setTitle(OtherPerson + " - IP Address: " + ip.getHostAddress());
			newchat.getText().append(OtherPerson + " (" + ip + ": " + port + ") " + message + "\n");
			newchat.setVisible(true);
		} else {
			ChatApp currentChat = newChat.get(ip);
			currentChat.setTitle(OtherPerson + " - IP Address: " + ip.getHostAddress());
			currentChat.getText().append(OtherPerson + " (" + ip + ": " + port + ") " + message + "\n");
			currentChat.setVisible(true);
		}
	}

	// Dissecting the Data-gram
	public static boolean RequestIP(String message) {

		// Other person
		if (message.startsWith("?????")) {
			String[] MessageSplit = message.split(" ");

			// Requested IP
			if (MessageSplit[2].equalsIgnoreCase("#####")) {
				RequestedIP = MessageSplit[1];
				OtherPerson = MessageSplit[3];
				return true;
			}
		}
		return false;
	}

	// Dissecting the Data-gram
	public static boolean RequestAnswer(String message) {

		// Other person
		if (message.startsWith("#####")) {

			// Other IP Information
			String[] MessageSplit = message.split(" ");
			if ((MessageSplit[1].equalsIgnoreCase(Receiver)) && MessageSplit[2].equalsIgnoreCase("#####")) {
				OtherPerson = MessageSplit[1];
				try {
					otherIP = InetAddress.getByName(MessageSplit[3]);
				} catch (UnknownHostException e) {

					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}

	// Returns the information
	public static Socket getSocket() { return socket; }
	public static String getMyName() { return MyName; }
	public static String getOtherName() { return OtherPerson; }
	public static InetAddress getOtherIP() { return otherIP; }
	public static int getCount() { return count; }
	public static Map<InetAddress, ChatApp> getNewChat() { return newChat; }

	// Something was clicked
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();

		switch (clicked.getText()) {

		// Broadcast was clicked
		case "Broadcast":
			InetAddress broadcast = null;

			// Broadcast to all the local network
			try {
				broadcast = InetAddress.getByName("255.255.255.255");
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			// Broadcast request and reset it
			Receiver = textfield3.getText();
			
			if(Receiver.equalsIgnoreCase(MyName)) {
				ChatWindow(myIP, port, "????? " + Receiver + " ##### " + MyName);
			}
			
			Socket.send("????? " + Receiver + " ##### " + MyName, broadcast, port);
			textfield3.setText("");			
			break;

		// Exit was clicked
		case "Close":
			System.exit(0);
			break;

		// Connect was clicked
		case "Connect":
			// if the input isn't empty, displays the input and creates chat window
			if (!textfield.getText().isEmpty() && !textfield2.getText().isEmpty()) {
				int portNum = Integer.parseInt(textfield2.getText());
				String ipNum = textfield.getText();

				// This is converting String to InetAddress
				InetAddress ip = null;
				try {
					ip = InetAddress.getByName(ipNum);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}

				// Displays the IP and Port I typed
				label5.setText("Input IP : " + ipNum);
				textfield.setText("");
				label6.setText("Input Port : " + portNum);
				textfield2.setText("");

				// Creates chatApp and put info in Hash-map
				ChatApp createChat = new ChatApp(socket, ip, portNum);
				System.out.println("System: " + ip + ": " + portNum + " - You have created a chat.");
				newChat.put(ip, createChat);
			}
			break;
		}
	}
}
