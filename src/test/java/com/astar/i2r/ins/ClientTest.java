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
		log.setLevel(Level.DEBUG);

		INS ins = new INS();

	}

	public static void main(String[] argv) {

		new ClientTest();

		JxMap window = new JxMap();
		EventQueue.invokeLater(window);

		// String sensorLogFileName = "sensor/1446089995.751188.txt";
		// String sensorLogFileName = "sensor/1446090161.558128.txt";
		String sensorLogFileName = "sensor/1446090536.171343.txt";

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
				Thread.sleep(1);
				if (lineCnt % 500 == 0) {
					String gps = query(RequestServer.RQTGPS);
					String mapname = query(RequestServer.RQTMAPNAME + "," + gps);
					log.info("GPS: " + gps + "\tMap name: " + mapname);

					String[] lle = gps.split(",");
					if (lle.length > 1) {
						double lat = Double.parseDouble(lle[0]);
						double lon = Double.parseDouble(lle[1]);
						if (mapname.compareTo("") == 0) {
							window.clearMarker();
							window.hideImage();
							window.setAddressLocation(lat, lon);
						} else {
							window.dispImage(mapname);
							window.setMarker(lat, lon);
						}

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

		// log.info(query(RequestServer.RQTGPS));
		// log.info(query(RequestServer.RQTLOC));

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
			log.trace(cmd);
			ans = reqBr.readLine();
			log.trace(ans);

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
