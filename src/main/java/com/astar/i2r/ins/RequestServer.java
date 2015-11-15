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
	public static final String RQTMAPPRMT = "RQTMAPPRMT";
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
					String mname = null;
					if (cmds.length > 3) {
						mname = CarParkDB.getMap(Double.parseDouble(cmds[1]),
								Double.parseDouble(cmds[2]),
								Double.parseDouble(cmds[3]));
					} else {
						GeoPoint gps = car.getGPS();
						double relativeAltitude = car.getRelativeAltitude();
						if (gps != null && !Double.isNaN(relativeAltitude)) {
							mname = CarParkDB.getMap(gps.lat, gps.lon,
									relativeAltitude);
						}
					}
					IOUtils.write(mname + '\n', dos);
				} else if (cmd.contains(RQTMAPPRMT)) {
					String[] cmds = cmd.split(",");
					double[] prmt = null;
					if (cmds.length > 1) {
						prmt = CarParkDB.getMapParameter(cmds[1]);
					}
					if (prmt != null) {
						IOUtils.write(prmt[0] + "," + prmt[1] + "," + prmt[2]
								+ "," + prmt[3] + '\n', dos);
					} else {
						IOUtils.write("Unknown Map " + cmds[1] + '\n', dos);
					}
				} else if (cmd.contains(RQTMAP)) {
					String[] cmds = cmd.split(",");
					if (cmds.length > 1) {
						FileInputStream fis = new FileInputStream(cmds[1]);
						int bytes = IOUtils.copy(fis, dos);
						fis.close();
						log.info("Server sent file " + cmds[1] + ", size "
								+ bytes + " bytes.");
					} else {
						GeoPoint gps = car.getGPS();
						double relativeAltitude = car.getRelativeAltitude();
						String mname = CarParkDB.getMap(gps.lat, gps.lon,
								relativeAltitude);
						dos.writeUTF(mname);
						FileInputStream fis = new FileInputStream(mname);
						int bytes = IOUtils.copy(fis, dos);
						fis.close();
						log.info("Server sent file " + mname + ", size "
								+ bytes + " bytes.");
					}
				} else if (cmd.contains(RQTLOC)) {
					String[] cmds = cmd.split(",");
					String locationName = null;
					if (cmds.length > 2) {
						locationName = getLocationName(
								Double.parseDouble(cmds[1]),
								Double.parseDouble(cmds[2]));
						if (locationName == null) {
							locationName = "Unknown location!";
						}
					} else {
						GeoPoint gps = car.getGPS();
						locationName = getLocationName(gps.lat, gps.lon);
						if (locationName == null) {
							locationName = "Unknown location!";
						}
					}
					IOUtils.write(locationName + '\n', dos);
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

	private String getLocationName(double lat, double lon) {

		LatLng p = new LatLng(Double.toString(lat), Double.toString(lon));
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
				.setLocation(p).setLanguage("en").getGeocoderRequest();
		GeocodeResponse geocoderResponse;
		try {
			geocoderResponse = geocoder.geocode(geocoderRequest);
			GeocoderResult result = geocoderResponse.getResults().get(0);

			return result.getFormattedAddress();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
}
