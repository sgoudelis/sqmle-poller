package gr.nerv.services.sqm.emulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Stratos Goudelis
 *
 */
public class SQMLE {
	
	String password = "pipes";          // does the device have a password?
	ServerSocket serverSocket = null;
	Socket clientSocket = null;
	int port = 10001;
	
	public String[] commands = {
			"rx",
			"cx",
			"ix"
	};
	
	public String[] replies  = {
			"r, %3.2fm,%010dHz,%010dc,%07.3fs,%03.2fs",
			"c,pipes,pipes",
			"i,pipes,pipes"
	};
	
	public SQMLE(int port, String password) {
		this.port = port;
		this.password = password;
	}
	
	public SQMLE(int port) {
		this.port = port;
		this.password = "";
	}
	
	public void waitForConnection() throws IOException {
		
		try {
		    this.serverSocket = new ServerSocket(this.port);
		} 
		catch (IOException e) {
		    System.out.println("Could not listen on port: "+this.port+" ("+e.getMessage()+")");
		    System.exit(-1);
		}
		
		try {
		    this.clientSocket = serverSocket.accept();
		}
		catch (IOException e) {
		    System.out.println("Accept failed: "+this.port+" ("+e.getMessage()+")");
		    System.exit(-1);
		}
		
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) { 
			int idx = Arrays.asList(this.commands).indexOf(inputLine);
			if(idx!=-1) {
				out.println(this.getReply(idx));
			}
		}
	}
	
	public float getRandMags() {
		Random rand = new Random();
		float maxMags = 25.00f;
		float minMags = 0.0f;
		float mags = 0;
		mags = rand.nextFloat() * (maxMags - minMags) + minMags;
		return mags;
	}
	
	public int getRandFreq() {
		Random rand = new Random();
		int maxFreq = 1000000;
		int minFreq = 1;
		int freq = 0;
		freq = rand.nextInt((maxFreq - minFreq) + minFreq);
		return freq;
	}
	
	public int getRandCounts() {
		Random rand = new Random();
		int maxFreq = 1000000;
		int minFreq = 1;
		int freq = 0;
		freq = rand.nextInt((maxFreq - minFreq) + minFreq);
		return freq;
	}
	
	public float getRandSeconds() {
		Random rand = new Random();
		float maxSeconds = 100000.00f;
		float minSeconds = 0.0f;
		float seconds = 0;
		seconds = rand.nextFloat() * (maxSeconds - minSeconds) + minSeconds;
		return seconds;
	}
	
	public float getRandTemperature () {
		Random rand = new Random();
		float maxTemp = 25.00f;
		float minTemp = 0.0f;
		float temp = 0;
		temp = rand.nextFloat() * (maxTemp - minTemp) + minTemp;
		return temp;
	}
	
	public String getReply(int idx) {
		String reply = "";
		reply = String.format(this.replies[idx], this.getRandMags(), this.getRandFreq(), this.getRandCounts(), this.getRandSeconds(), this.getRandTemperature());
		return reply;
	}
	
	public boolean disconect() {
		try {
			this.serverSocket.close();
			return true;
		} catch (IOException e) {
			System.out.println(String.format("Could not close server socket (%s)", e.getMessage()));
			e.printStackTrace();
			return false;
		}
	}
}
