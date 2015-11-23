package com.astar.i2r.ins;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.astar.i2r.ins.gui.JxMap;

public class ClientTest {

	private static final Logger log = Logger.getRootLogger();

	public ClientTest() {

		BasicConfigurator.configure();
		log.setLevel(Level.INFO);

		INS ins = new INS();

	}

	public static void main(String[] argv) {

		new ClientTest();

		String lastMap = "";

		JxMap window = new JxMap();
		EventQueue.invokeLater(window);

		// String sensorLogFileName = "sensor/1446089995.751188.txt";
		// String sensorLogFileName = "sensor/1446090161.558128.txt";

		// String sensorLogFileName = "sensor/1447040772.693506.txt";

		// String sensorLogFileName = "sensor/1444893828.912658-withGT.txt";
		// String sensorLogFileName = "sensor/1446090536.171343-withGT.txt";
		 String sensorLogFileName = "sensor/1447040772.693506-withGT.txt";

		try {
			Socket dataSock = new Socket("localhost", DataServer.PORT);
			DataOutputStream dataOs = new DataOutputStream(
					dataSock.getOutputStream());
			BufferedReader dataBr = new BufferedReader(new FileReader(
					sensorLogFileName));
			String line;

			int lineCnt = 0;
			while ((line = dataBr.readLine()) != null) {
				lineCnt++;
				IOUtils.write(line + '\n', dataOs);

				if (lineCnt % 100 == 0) {
					Thread.sleep(50);
					String[] gps = query(RequestServer.RQTGPS).split(",");
					String mapname = query(RequestServer.RQTMAPNAME);

					if (gps.length > 1) {
						double lat = Double.parseDouble(gps[0]);
						double lon = Double.parseDouble(gps[1]);
						if (lastMap.compareTo(mapname) != 0) {
							if (mapname.compareTo("") == 0) {
								window.hideImage();
							} else {

								String[] prmt = query(
										RequestServer.RQTMAPPRMT + ","
												+ mapname).split(",");
								double[] scale = new double[] {
										Double.parseDouble(prmt[0]),
										Double.parseDouble(prmt[1]) };
								double[] reference = new double[] {
										Double.parseDouble(prmt[2]) - 0.000075,
										Double.parseDouble(prmt[3]) - 0.000075 };
								// Double.parseDouble(prmt[2]),
								// Double.parseDouble(prmt[3]) };
								window.dispImage(mapname, scale, reference);
							}
							lastMap = mapname;
						}
						window.setAddressLocation(lat, lon);
					}
				}
			}

			dataSock.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String query(String cmd) {
		String ans = null;
		try {
			Socket reqSock = new Socket("localhost", RequestServer.PORT);

			BufferedReader reqBr = new BufferedReader(new InputStreamReader(
					new DataInputStream(reqSock.getInputStream())));
			DataOutputStream reqOs = new DataOutputStream(
					reqSock.getOutputStream());

			IOUtils.write(cmd + '\n', reqOs);
			log.trace("Send query to server: " + cmd);
			ans = reqBr.readLine();
			log.trace("Received answer from server: " + ans);

			reqOs.flush();
			reqOs.close();
			reqBr.close();
			reqSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;
	}

}
