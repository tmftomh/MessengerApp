import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private JList userList;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;

	// constructor
	public Client(String host) {
		super("Instant Messager Version 1 (Client)");
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				 sendMessage(event.getActionCommand());
				 userText.setText("");
			}
		});
		chatWindow = new JTextArea();
		setSize(640, 480);
		setLocationRelativeTo(null);
		
		// Column One
		
		gc.weightx = 0.5;
		gc.weighty = 0.5;
				
		gc.gridx = 0;
		gc.gridy = 0;
		
		gc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(chatWindow), gc);
		
        // Row 2
		
		gc.weightx = 1;
		gc.weighty = 0;
		
		gc.gridx = 0;
		gc.gridy = 1;	
		
		add(userText, gc);
		
		// Column 2
		
		gc.gridx = 1;
		gc.gridy = 0;
		
		add(userList, gc);
		setVisible(true);
		
	}

	public void startRunning() {

		try {
			connectToServer(); // connect and have conversation
			setupStreams();
			whileChatting();
		} catch (EOFException eofException) {
			showMessage("\n Server ended the connection");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeMess();
		}
	}

	private void connectToServer() throws IOException{
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}

	// during the chat conversation
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try { // have a conversation
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n Invalid message");
			}
		} while (!message.equals("SERVER - END"));
	}

	// close streams and sockets after you are done chating
	private void closeMess() {
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// send a message to client
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		} catch (IOException ioException) {
			chatWindow.append("\n ERROR: Message cannot be sent");
		}
	}

	// updates chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(text);
			}
		});
	}

	// let the user type stuff into their box
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(tof);
			}
		});
	}

}
