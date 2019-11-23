import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class InputUI implements ActionListener {

	public static JLabel label7;
	public static JTextField textfield1;
	public static JFrame Input;
	public static JButton Submit;
	public static String MyName = " ";
	public static int count = 1;

	// This starts the new InputUI
	public static void main(String[] args) {
		InputUI start = new InputUI(); 
	}
	
	// Fresh GUI that takes Name Input
	public InputUI() {
		Input = new JFrame();
		Input.setTitle("Chat Application");

		label7 = new JLabel("Your Name: ");
		label7.setBounds(10, 10, 100, 100);
		Input.add(label7);

		textfield1 = new JTextField();
		textfield1.setBounds(110, 45, 130, 30);
		Input.add(textfield1);

		Submit = new JButton("Submit");
		Submit.setBounds(100, 170, 80, 40);
		Input.add(Submit);
		Submit.addActionListener(this);

		Input.setSize(300, 400);
		Input.setLayout(null);
		Input.setVisible(true);
		Input.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// Submit was clicked
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();

		switch (clicked.getText()) {

		// Typed your name
		case "Submit":
			MyName = textfield1.getText();
			count = 2;
			Input.dispose();
			ChatUI startChat = new ChatUI();
			break;
		}
	}

	// Returns the information
	public static int getCount2() { return count; }
	public static String getMyName() { return MyName; }
}
