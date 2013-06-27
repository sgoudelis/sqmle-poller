package gr.nerv.services.sqm.poller;

import java.net.*;
import java.io.*;

/**
 * SQM-LE connector class
 * Provides low-level communication methods to the device
 * @author Stratos Goudelis
 * Based on tcpip.java from Unihedron
 *
 */
public class SQMLEConnector {
	protected Socket socket = null;
	public DataInputStream socketInput = null;
	protected DataOutputStream socketOutput = null;
	protected String ip;
	protected int port;
	protected String password;
	private InetAddress ipa;
	protected boolean passwordSend;
	protected int loopTime = 50;
	protected int waitTimeThreshold = 2000;

	/**
	 * @param ip
	 * @param port
	 * @param password
	 * @throws SQMLEException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public SQMLEConnector(String ip, int port, String password)
			throws SQMLEException, UnknownHostException, IOException {
		
		Socket testSocket = null;
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.passwordSend = false;

		try {
			this.ipa = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			throw new SQMLEException("Error connecting to SQM-LE device ("+e.getMessage()+")");
		}

		testSocket = new Socket(ipa.getHostAddress(), port);
		testSocket.setKeepAlive(true);

		socket = testSocket;
		try { // Create an input stream
			socketInput = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
		} catch (Exception ex) {
			throw new SQMLEException("Error creating input stream");
		}
		try { // Create an output stream
			socketOutput = new DataOutputStream(new BufferedOutputStream(
					socket.getOutputStream()));
		} catch (Exception ex) {
			throw new SQMLEException("Error creating output stream");
		}
	}

	/**
	 * @throws SQMLEException
	 */
	public synchronized void sendPassword() throws SQMLEException {
		if (!this.password.isEmpty()) {
			// check if device asks for password
			String buffer = "";
			try {
				buffer = this.receive(true, 500);
			} catch (SQMLEException e) {
				e.printStackTrace();
			} catch (SQMLETimeOutException e) {

			}

			if (buffer.indexOf("Password") != -1) {
				// device is asking for a password
				try {
					this.send(this.password);
					this.receive(true, 100);
					this.passwordSend = true;
				} catch (SQMLEException e) {
					throw new SQMLEException("Could not send password");
				} catch (SQMLETimeOutException e) {
					System.out.println("SQMLETimeOutException occured for cmd");
				}
			}
		}
	}

	/**
	 * @param cmd
	 * @param timeout
	 * @return
	 * @throws SQMLEException
	 */
	public synchronized String sendCmd(String cmd, int timeout)
			throws SQMLEException {
		String response = null;
		this.send(cmd);
		try {
			response = this.receive(false, timeout);
		} catch (SQMLETimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 */
	public synchronized void disconnect() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * @param temp
	 * @throws SQMLEException
	 */
	public synchronized void send(byte[] temp) throws SQMLEException {
		try {
			socketOutput.write(temp, 0, temp.length);
			socketOutput.flush();
		} catch (Exception ex) {
			throw new SQMLEException("Error sending data : " + ex.toString());
		}
	}

	/**
	 * @param temp
	 * @param len
	 * @throws SQMLEException
	 */
	public synchronized void send(byte[] temp, int len) throws SQMLEException {
		try {
			socketOutput.write(temp, 0, len);
			socketOutput.flush();
		} catch (Exception ex) {
			throw new SQMLEException("Error sending data : " + ex.toString());
		}
	}

	/**
	 * @param given
	 * @throws SQMLEException
	 */
	public synchronized void send(String given) throws SQMLEException {
		given = given + "\r\n";
		int length = given.length();
		byte[] retvalue = new byte[length];
		char[] c = new char[length];
		given.getChars(0, length, c, 0);
		for (int i = 0; i < length; i++) {
			retvalue[i] = (byte) c[i];
		}
		send(retvalue);
	}

	/**
	 * @param forgive
	 * @param timeout
	 * @return
	 * @throws SQMLEException
	 * @throws SQMLETimeOutException
	 */
	public synchronized String receive(boolean forgive, int timeout)
			throws SQMLEException, SQMLETimeOutException {

		byte[] retval = new byte[0];
		int totalWaitTime = 0;
		try {
			while (socketInput.available() == 0) {
				// waiting
				try {
					Thread.sleep(this.loopTime);
					totalWaitTime = totalWaitTime + this.loopTime;
					if (totalWaitTime >= timeout && !forgive) {
						// no answer within time period, close everything
						this.socketInput.close();
						this.socketOutput.close();
						this.socket.close();
						throw new SQMLEException(
								"Connection closed or wrong password");
					} else if (totalWaitTime >= timeout && forgive) {
						// if timeout expired return empty string, the server
						// did not say anything
						throw new SQMLETimeOutException(
								"Did not receive a reply fast enough. Please adjust replyTimout?");
					}
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (IOException e) {
			throw new SQMLEException(e);
		}
		try {
			retval = new byte[socketInput.available()];
		} catch (IOException e) {
			throw new SQMLEException(e);
		}
		try {
			socketInput.read(retval);
		} catch (IOException e) {
			throw new SQMLEException(e);
		}

		// convert to String
		String response = new String(retval);
		return (response);
	}

	/**
	 * @return
	 * @throws SQMLEException
	 */
	public int available() throws SQMLEException {
		int avail;
		avail = 0;
		try {
			avail = socketInput.available();
		} catch (IOException e) {
			System.out.println("not avail");
			throw new SQMLEException(e);
		}
		return (avail);
	}
}
