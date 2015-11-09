package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.astar.i2r.ins.localization.Context;
import com.astar.i2r.ins.map.CarParkDB;
import com.astar.i2r.ins.map.GeoMap;
import com.astar.i2r.ins.motion.GeoPoint;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.LatLng;

public class RequestServer extends Thread {
	private static final Logger log = Logger.getLogger(RequestServer.class
			.getName());

	public static final String RQTGPS = "RQTGPS";
	public static final String RQTMAP = "RQTMAP";
	public static final String RQTMAPNAME = "RQTMAPNAME";
	public static final String RQTLOC = "RQTLOC";

	private GeoMap map = null;
	private Map<Integer, Context> cList = null;
	private ServerSocket rSockSvr = null;
	public static final int PORT = 2001;

	private static final Geocoder geocoder = new Geocoder();

	public RequestServer(Map<Integer, Context> _cList, GeoMap _map) {

		map = _map;
		cList = _cList;
		try {
			rSockSvr = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log.fatal("Cannot open server port " + PORT);
		}
	}

	@Override
	public void run() {
		String cmd = null;
		while (true) {
			Socket rSock = null;
			try {
				rSock = rSockSvr.accept();
				log.trace("Link established.");
				// cmd = IOUtils.toString(rSock.getInputStream());
				DataOutputStream dos = new DataOutputStream(
						rSock.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new DataInputStream(rSock.getInputStream())));

				cmd = br.readLine();
				if (cmd == null) {
					break;
				}

				Context car = cList.get(INS.car0);

				log.trace("Received: " + cmd);
				if (cmd.contains(RQTGPS)) {
					GeoPoint curPos = car.getGPS();
					if (curPos == null) {
						IOUtils.write("GPS coordinates Unknow.\n", dos);
					} else {
						IOUtils.write(car.getGPS().toString() + '\n', dos);
					}
				} else if (cmd.contains(RQTMAPNAME)) {
					String[] cmds = cmd.split(",");
					if (cmds.length > 3) {
						String mname = CarParkDB.getMap(cmds[1], cmds[2],
								cmds[3]);
						IOUtils.write(mname + '\n', dos);
					}
				} else if (cmd.contains(RQTMAP)) {
					String[] cmds = cmd.split(",");
					if (cmds.length > 1) {
						FileInputStream fis = new FileInputStream(cmds[1]);
						int bytes = IOUtils.copy(fis, dos);
						fis.close();
						System.out.println("Server sent file size: " + bytes);
					}
				} else if (cmd.contains(RQTLOC)) {
					String[] cmds = cmd.split(",");
					if (cmds.length > 2) {
						GeoPoint curPos = new GeoPoint(
								Double.parseDouble(cmds[1]),
								Double.parseDouble(cmds[2]), 0, 0);
						LatLng p = new LatLng(Double.toString(curPos.lat),
								Double.toString(curPos.lon));
						GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
								.setLocation(p).setLanguage("en")
								.getGeocoderRequest();
						GeocodeResponse geocoderResponse = geocoder
								.geocode(geocoderRequest);
						GeocoderResult result = geocoderResponse.getResults()
								.get(0);

						IOUtils.write(result.getFormattedAddress() + '\n', dos);
					}
				} else {
					String invalid = "Invalid request!";
					IOUtils.write(invalid + '\n', dos);
					log.debug("Received invalid request from client.");
				}
				dos.flush();
				dos.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
