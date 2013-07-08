package gr.nerv.services.sqm.emulator;

import java.io.IOException;


/**
 * @author Stratos Goudelis
 *
 */
public class SQMLEEmulator {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		while(true) {
			SQMLE sqmleEmu = new SQMLE(10001);
			try {
				sqmleEmu.waitForConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sqmleEmu.disconect();
			sqmleEmu = null;
		}
	}
}
