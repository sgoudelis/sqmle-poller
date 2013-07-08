package gr.nerv.services.sqm.poller;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * SQM-LE poller application
 * Uses SQMLE class for communication
 * @author Stratos Goudelis
 *
 */
public class SQMLEPoller {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SQMLE sqmle;
		String configFile = "poller.properties";

		// new properties object
		Properties settings = new Properties();
		
		try {
			// load a properties file
			settings.load(new FileInputStream(configFile));
		} catch (NullPointerException settingsEx) {
			System.out.println("pipes2");
			settingsEx.printStackTrace();
		} catch (FileNotFoundException settingsEx) {
			System.out.println("Could not find properties file: "+configFile);
		} catch (IOException settingsEx) {
			System.out.println("pipes");
			settingsEx.printStackTrace();
		}
		
		while (true) {
			String deviceHost = settings.getProperty("host");
			int devicePort = Integer.parseInt(settings.getProperty("port"));
			String devicePassword = settings.getProperty("password");
			
			sqmle = new SQMLE(deviceHost, devicePort, devicePassword);
			try {
				sqmle.getReadings();
			} catch (UnknownHostException e) {
				System.out.println(String.format("Could not resolve host %s:%s (%s)", deviceHost, devicePort, e.getMessage()));
				break;
			} catch (ConnectException e) {
				System.out.println(String.format("Could not connect to SQM-LE device %s:%s (%s)", deviceHost, devicePort, e.getMessage()));
				break;
			} catch (SQMLEPasswordException e) {
				System.out.println(String.format("Could not send password to SQM-LE device %s:%s (%s)", deviceHost, devicePort, e.getMessage()));
				break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			
			System.out.println("serial: " + sqmle.serialNumber + " "
					+ sqmle.magPerSqAS + "mag/arcsec^2 " + sqmle.frequency
					+ "Hz " + sqmle.periodCount + "c " + sqmle.periodSeconds
					+ "s " + sqmle.temperature + "C");
			
			sqmle = null;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
