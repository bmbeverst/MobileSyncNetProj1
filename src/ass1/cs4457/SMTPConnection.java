package ass1.cs4457;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 * 
 */
public class SMTPConnection {
	/* The socket to the server */
	private Socket connection;

	/* Streams for reading and writing the socket */
	private BufferedReader fromServer;
	private DataOutputStream toServer;

	private static final int SMTP_PORT = 25;
	private static final String CRLF = "\r\n";

	/* Are we connected? Used in close() to determine what to do. */
	private boolean isConnected = false;

	/*
	 * Create an SMTPConnection object. Create the socket and the associated
	 * streams. Initialize SMTP connection.
	 */
	public SMTPConnection(Envelope envelope) throws IOException {
		connection = new Socket(envelope.DestAddr, SMTP_PORT);
		// 128.143.2.232 bmb2gf@virgina.edu
		fromServer = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());

		/* Fill in */
		/*
		 * Read a line from server and check that the reply code is 220. If not,
		 * throw an IOException.
		 */
		/* Fill in */
		System.out.println(envelope.toString());
		String serverLine = fromServer.readLine();
		System.out.println(serverLine);
		
		if (parseReply(serverLine) == 220) {
			/*
			 * SMTP handshake. We need the name of the local machine. Send the
			 * appropriate SMTP handshake command.
			 */
			String localhost = "";
			InetAddress[] all = InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName());
			for (int i = 0; i < all.length; i++) {
				// check valid ip
				if (all[i].getHostAddress().indexOf('.') != -1) {
					if (InetAddress.getByName(all[i].getHostAddress())
							.isReachable(1000)) {
						localhost = all[i].getHostAddress();
					}
				}
			}
			sendCommand("EHLO " + localhost, 250);
			isConnected = true;
		} else {
			throw new IOException("Sever Connection Failed");
		}
	}

	/*
	 * Send the message. Write the correct SMTP-commands in the correct order.
	 * No checking for errors, just throw them to the caller.
	 */
	public void send(Envelope envelope) throws IOException {
		/* Fill in */
		/*
		 * Send all the necessary commands to send a message. Call sendCommand()
		 * to do the dirty work. Do _not_ catch the exception thrown from
		 * sendCommand().
		 */
		/* Fill in */
		if (!isConnected) {
			throw new IOException("No Sever Connection!");
		}
		sendCommand("MAIL FROM " + envelope.Sender, 250);
		sendCommand("RCPT TO " + envelope.Recipient, 250);
		sendCommand("DATA " + envelope.Recipient, 250);
	}

	/*
	 * Close the connection. First, terminate on SMTP level, then close the
	 * socket.
	 */
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 221);
			connection.close();
		} catch (IOException e) {
			System.out.println("Unable to close connection: " + e);
			isConnected = true;
		}
	}

	/*
	 * Send an SMTP command to the server. Check that the reply code is what is
	 * is supposed to be according to RFC 821.
	 */

	// 128.143.2.232 bmb2gf@virgina.edu
	private void sendCommand(String command, int rc) throws IOException {
		/* Fill in */
		/* Write command to server and read reply from server. */
		/* Fill in */
		System.out.println(command);// TODO remove
		toServer.writeBytes(command);
		toServer.flush();

		/* Fill in */
		/*
		 * Check that the server's reply code is the same as the parameter rc.
		 * If not, throw an IOException.
		 */
		/* Fill in */
		
		String readLine = fromServer.readLine();
		System.out.println(readLine);// TODO remove
		if (parseReply(readLine) != rc) {
			throw new IOException("Malformed command");
		}

	}

	/* Parse the reply line from the server. Returns the reply code. */
	private int parseReply(String reply) {
		int space = reply.indexOf(' ');
		String replyCode = reply.substring(0, space);
		return Integer.parseInt(replyCode);
	}

	/* Destructor. Closes the connection if something bad happens. */
	@Override
	protected void finalize() throws Throwable {
		if (isConnected) {
			close();
		}
		super.finalize();
	}
}
