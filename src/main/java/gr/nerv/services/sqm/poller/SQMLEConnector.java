package gr.nerv.services.sqm.poller;
import java.net.*;
import java.io.*;

public class SQMLEConnector {
	protected Socket socket = null;
	public DataInputStream dis = null;
	protected DataOutputStream dos = null;
	protected String ip;
	protected int port;
	protected String password;
	private InetAddress ipa;
	protected boolean passwordSend;
	protected int loopTime = 50; 
	protected int waitTimeThreshold = 2000;
	
	public SQMLEConnector(String ip, int port, String password) throws SQMLEException, UnknownHostException, IOException {
		Socket s1 = null;
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.passwordSend = false;
		
		try {
			this.ipa = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			throw new SQMLEException("Error connecting to SQM-LE device (could resolve host)");
		}
		
		s1 = new Socket(ipa.getHostAddress(), port);
		s1.setKeepAlive(true);
		
		socket = s1;
		try { // Create an input stream
			dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (Exception ex) {
			throw new SQMLEException("Error creating input stream");
		}
		try { // Create an output stream
			dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (Exception ex) {
			throw new SQMLEException("Error creating output stream");
		}
	}

	public synchronized void sendPassword() throws SQMLEException {
		if(!this.password.isEmpty()) {
			// check if device asks for password
			String buffer = "";
			try {
				buffer = this.receive(true, 500);
			} catch (SQMLEException e) {
				e.printStackTrace();
			} catch (SQMLETimeOutException e) {
				
			}
			
			if(buffer.indexOf("Password")!=-1) {
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
	
	/* (non-Javadoc)
	 * @see gr.hiverse.services.sqmle.poller.SQMLEInterface#sendCmd(java.lang.String)
	 */
	public synchronized String sendCmd(String cmd, int timeout) throws SQMLEException {
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
	
	/* (non-Javadoc)
	 * @see gr.hiverse.services.sqmle.poller.SQMLEInterface#disconnect()
	 */
	public synchronized void disconnect() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public synchronized void send(byte[] temp) throws SQMLEException {
		try {
			dos.write(temp, 0, temp.length);
			dos.flush();
		} catch (Exception ex) {
			throw new SQMLEException("Error sending data : " + ex.toString());
		}
	}

	public synchronized void send(byte[] temp, int len) throws SQMLEException {
		try {
			dos.write(temp, 0, len);
			dos.flush();
		} catch (Exception ex) {
			throw new SQMLEException("Error sending data : " + ex.toString());
		}
	}

	/* (non-Javadoc)
	 * @see gr.hiverse.services.sqmle.poller.SQMLEInterface#send(java.lang.String)
	 */
	public synchronized void send(String given) throws SQMLEException {
		given = given+"\r\n";
		int length = given.length();
		byte[] retvalue = new byte[length];
		char[] c = new char[length];
		given.getChars(0, length, c, 0);
		for (int i = 0; i < length; i++) {
			retvalue[i] = (byte) c[i];
		}
		send(retvalue);
	}

	/* (non-Javadoc)
	 * @see gr.hiverse.services.sqmle.poller.SQMLEInterface#receive(boolean)
	 */
	public synchronized String receive(boolean forgive, int timeout) throws SQMLEException, SQMLETimeOutException {

		byte[] retval = new byte[0];
		int totalWaitTime = 0;
		try {
			while (dis.available() == 0) {
				// waiting
				try {
				    Thread.sleep(this.loopTime);
				    totalWaitTime = totalWaitTime + this.loopTime;
				    if(totalWaitTime >= timeout && !forgive) {
				    	// no answer within time period, close everything
				    	this.dis.close();
				    	this.dos.close();
				    	this.socket.close();
				    	throw new SQMLEException("Connection closed or wrong password");
				    } else if(totalWaitTime >= timeout && forgive) {
				    	// if timeout expired return empty string, the server did not say anything
				    	throw new SQMLETimeOutException("Did not receive a reply fast enough. Please adjust replyTimout?");
				    }
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			} 
		} catch (IOException e) {
			throw new SQMLEException(e);
		}
		try {
			retval = new byte[dis.available()];
		} catch (IOException e) {
			throw new SQMLEException(e);
		}
		try {
			dis.read(retval);
		} catch (IOException e) {
			throw new SQMLEException(e);
		}
		
		// convert to String
		String response = new String(retval);
		return (response);
	}

	/* (non-Javadoc)
	 * @see gr.hiverse.services.sqmle.poller.SQMLEInterface#available()
	 */
	public int available() throws SQMLEException {
		int avail;
		avail = 0;
		try {
			avail = dis.available();
		} catch (IOException e) {
			System.out.println("not avail");
			throw new SQMLEException(e);
		}
		return (avail);
	}
}
