import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.ArrayList;
import java.util.Enumeration;


public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
	//SerialPortEvent oEvent;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
		"/dev/tty.usbserial-A9007UX1", // Mac OS X
		"/dev/ttyPS0", // Linux
		"COM11", // Windows
	};
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 20000;
	private static final int DATA_RATE = 115200;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}


		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			//    System.out.println("reader: "+input.readLine());
			output = serialPort.getOutputStream();

			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}


	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=null;
				if (input.ready()) {
					inputLine = input.readLine();
					System.out.println(inputLine);
				}

			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE){
			//  if (inputLine== "loserville")
				File fin = new File("lib\\log.csv");
				try{
					ArrayList<String> outputList=	readFile(fin);

					for (int i = 0; i < outputList.size(); i++) {
						String outputLine=" "+outputList.get(i)+ "\n";

						System.out.println("out: "+outputLine);
						output.write(outputLine.getBytes());}

				}catch (Exception e){
					System.err.println(e.toString());
				}
		}  
	} 


	// Ignore all the other eventTypes, but you should consider the other ones.

	private static ArrayList<String> readFile(File fin) throws IOException {
		FileInputStream fis = new FileInputStream(fin);
		ArrayList<String> fileList= new ArrayList<String>();
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		while ((line = br.readLine()) != null) {
//			System.out.println(line);
			fileList.add(line);
		}

		//	br.close();
		return fileList;
		//System.out.println(line);
	}
	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000    seconds,
				//waiting for events to occur and responding to them    (printing incoming messages to console).
				try {Thread.sleep(100000000);} catch (InterruptedException    ie) {System.err.println(".."+ie.getMessage());}
			}
		};
		t.start();
		System.out.println("Started");

	}
}