import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Socket {

	// Socket related initial creation
	public static int myPortNumber;
	public static InetAddress myAddress;
	public static DatagramSocket mySocket;
	public static Thread receiveThread;
	public static boolean Running = true;

	// Broadcast related initial creation
	public static String Username, MyUsername = ChatUI.getMyName(), YourUsername, AnotherIP, MyName = ChatUI.getMyName();
	public static InetAddress YourIPAddress = null, myIPAddress = null;
	public static DatagramSocket mysocket = null;
	public static Map<InetAddress, ChatApp> newChat = ChatUI.getNewChat();
	public static enum SocketType {Broadcast, NoBroadcast};

	// Data-gram is in queue method
	public static ConcurrentLinkedQueue<DatagramPacket> messageQueue = new ConcurrentLinkedQueue<DatagramPacket>();

	// Socket function
	public Socket(int myPortNumber, SocketType socketType) {
		Socket.myPortNumber = myPortNumber;

		try {
			myAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("System: My IP is = " + myAddress);
		
		try {
			switch (socketType) {
				case Broadcast:
					this.mySocket = new DatagramSocket(myPortNumber);				
					break;
				case NoBroadcast:	
					this.mySocket = new DatagramSocket(myPortNumber, myAddress);
					break;
			}
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(-1);
		}

		Socket.receiveThread = new Thread(new Runnable() {
			public void run() {
				receiveThreadMethod();
			}
		});
		receiveThread.setName("Receive Thread");
		receiveThread.start();
	}

	// Sends the message
	public static void send(String message, InetAddress destinationAddress, int destinationPort) {

		byte[] outBuffer = new byte[1024];
		String msg = message;
		outBuffer = msg.getBytes();
		
		try {
			DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress, destinationPort);
			System.out.println("\nSystem: Sent to = " + destinationAddress + ": " + destinationPort);
			System.out.println("System: You sent = " + msg);
			
			mySocket.send(outPacket);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}

	// Receives the message
	public static void receiveThreadMethod() {

		DatagramPacket inPacket = null;
		byte[] inBuffer = new byte[1024];
		inPacket = new DatagramPacket(inBuffer, inBuffer.length);
		System.out.println("System: Receive Thread is Starting");
		
		do {
			for (int i = 0; i < inBuffer.length; i++) {
				inBuffer[i] = ' ';
			}
			
			try {
				//inPacket = receive();
				mySocket.receive(inPacket);
				messageQueue.add(inPacket);
				
			} catch (SocketTimeoutException ste) {
				// Nothing to do
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(-1);
			}

			// This is added part by professor
			InetAddress ip = inPacket.getAddress();
			String message = new String(inPacket.getData());
			int senderPort = inPacket.getPort();
			
			System.out.println("\nSystem: Sender = " + ip.getHostAddress() + ": " + senderPort);
			System.out.println("System: Message = " + message);
			
			// Broadcast
			// If someone is requesting for my name, I reply back
			if ((ChatUI.RequestIP(message)) && (ChatUI.RequestedIP.equalsIgnoreCase(MyName))) {
				Socket.send("##### " + MyName + " ##### " + myAddress.getHostAddress(), ip, myPortNumber);
				System.out.println("System: " + "You replied = " + "##### " + MyName + " ##### " + myAddress.getHostAddress());
				ChatUI.ChatWindow(ip, senderPort, message);
			}

			// If someone is requesting my IP
			else if (ChatUI.RequestAnswer(message)) {
				ChatUI.ChatWindow(ChatUI.getOtherIP(), senderPort, message);
			}

			// Connect
			// If I'm already talking to the person, it just adds to that panel
			else {
				ChatUI.ChatWindow(ip, senderPort, message);
			}
			
		} while (true);
	}

	// Receive queue
	public static DatagramPacket receive() {
		return messageQueue.poll();
	}
	
	// Returns the information
	public int getMyPortNumber() { return myPortNumber; }
	public InetAddress getMyAddress() { return myAddress; }
}
