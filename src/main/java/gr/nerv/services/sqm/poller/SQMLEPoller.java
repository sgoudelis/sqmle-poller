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
			sqmle = new SQMLE(settings.getProperty("host"), Integer.parseInt(settings.getProperty("port")), settings.getProperty("password"));
			try {
				sqmle.getReadings();
			} catch (ConnectException e) {
				System.out.println("Could not connect to SQM-LE device ("+e.getMessage()+")");
				break;
			} catch (UnknownHostException e) {
				System.out.println("Could not connect to SQM-LE device ("+e.getMessage()+")");
				break;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
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
