package gr.nerv.services.sqm.poller;

import java.net.ConnectException;
import java.net.UnknownHostException;

public class SQMLEPoller {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SQMLE sqmle;

		while (true) {
			sqmle = new SQMLE("sqm-le.nerv.home", 10001, "teleport");
			//sqmle = new SQMLE("localhost", 10001, "teleport");
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
