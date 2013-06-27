package gr.nerv.services.sqm.poller;
import java.io.StringReader;
import au.com.bytecode.opencsv.CSVReader;

public class SQMLE {
	protected String ip;
	protected int port;
	protected String password;
	protected SQMLEConnector connector;

	protected int protocolVersion;
	protected int modelVersion;
	protected int featureNumber;
	protected int serialNumber;

	protected float magPerSqAS;
	protected long frequency;
	protected long periodCount;
	protected double periodSeconds;
	protected float temperature;

	protected int replyTimeout = 200; 
	
	/**
	 * @param args
	 * @throws Exception
	 */
	/**
	 * @param ip
	 * @param port
	 * @param password
	 */
	public SQMLE(String ip, int port, String password) {
		this.ip = ip;
		this.port = port;
		this.password = password;
	}
	
	/**
	 * @param ip
	 * @param port
	 */
	public SQMLE(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.password = "";
	}

	/**
	 * @throws Exception
	 */
	public void getReadings() throws Exception {
		this.connector = new SQMLEConnector(ip, port, password);

		try {
			this.connector.sendPassword();
		} catch (SQMLEException e) {
			e.printStackTrace();
		}
		
		Thread.sleep(100);
		
		// take infos
		String infoReply = "";
		try {
			infoReply = this.connector.sendCmd("ix", this.replyTimeout);
			StringReader reader = new StringReader(infoReply);
			CSVReader csvreader = new CSVReader(reader);
			String sampleList[] = csvreader.readNext();
			reader.close();
			csvreader.close();

			this.protocolVersion = Integer.parseInt(sampleList[1]);
			this.modelVersion = Integer.parseInt(sampleList[2]);
			this.featureNumber = Integer.parseInt(sampleList[3]);
			this.serialNumber = Integer.parseInt(sampleList[4]);
		} catch (SQMLEException e) {
			throw new Exception("Something went wrong sending command ix: "
					+ e.getMessage());
		}

		// take initial reading
		String sampleReply = "";
		try {
			sampleReply = this.connector.sendCmd("rx", replyTimeout);
			StringReader reader = new StringReader(sampleReply);
			CSVReader csvreader = new CSVReader(reader);
			String sampleList[] = csvreader.readNext();
			reader.close();
			csvreader.close();

			this.magPerSqAS = Float.parseFloat(sampleList[1].substring(0, 6));
			this.frequency = Long.parseLong(sampleList[2].substring(0, 10));
			this.periodCount = Long.parseLong(sampleList[3].substring(0, 10));
			this.periodSeconds = Double.parseDouble(sampleList[4].substring(0,
					10));
			this.temperature = Float.parseFloat(sampleList[5].substring(0, 4));
		} catch (SQMLEException e) {
			throw new Exception("Something went wrong sending command rx: "
					+ e.getMessage());
		}
		this.connector.disconnect();
	}

}
