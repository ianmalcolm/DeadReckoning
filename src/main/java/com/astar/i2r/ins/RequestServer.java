package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.astar.i2r.ins.localization.Context;
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

	private Map<Integer, Context> cList = null;
	private ServerSocket rSockSvr = null;
	public static final int PORT = 2001;

	private static final Geocoder geocoder = new Geocoder();

	public RequestServer(Map<Integer, Context> _cList) {

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

				log.trace("Received query from client: " + cmd);
				if (cmd.contains(RQTGPS)) {
					GeoPoint curPos = car.getGPS();
					if (curPos == null) {
						IOUtils.write("GPS coordinates Unknow.\n", dos);
						log.trace("RQTGPS: GPS coordinates Unknow.");
					} else {
						IOUtils.write(car.getGPS().toString() + '\n', dos);
						log.trace("RQTGPS: Sent gps to client : "
								+ car.getGPS().toString());
					}
				} else if (cmd.contains(RQTMAPNAME)) {
					String mapFileName = car.getMapFileName();
					if (mapFileName == null) {
						mapFileName = "";
					}
					IOUtils.write(mapFileName + '\n', dos);
					log.trace("RQTMAPNAME: Sent mapname to client: "
							+ mapFileName);
				} else if (cmd.contains(RQTMAPPRMT)) {
					double[] prmt = car.getMapParameter();
					String ans = null;
					if (prmt != null) {
						ans = prmt[0] + "," + prmt[1] + "," + prmt[2] + ","
								+ prmt[3];
					} else {
						ans = "Unknown Map ";
					}
					IOUtils.write(ans + '\n', dos);
					log.trace("RQTMAPPRMT: Sent parameter to client: " + ans);
				} else if (cmd.contains(RQTMAP)) {
					String mname = car.getMapFileName();
					if (mname != null) {
						dos.writeUTF(mname);
						FileInputStream fis = new FileInputStream(mname);
						int bytes = IOUtils.copy(fis, dos);
						fis.close();
						log.info("RQTMAP: Sent filename " + mname + ", size "
								+ bytes + " bytes.");
					} else {
						log.info("RQTMAP: Fetch map failed");
					}
				} else if (cmd.contains(RQTLOC)) {
					String locationName = getLocationName(car.getGPS().lat,
							car.getGPS().lon);
					if (locationName == null) {
						locationName = "Unknown location!";
					}
					IOUtils.write(locationName + '\n', dos);
					log.trace("RQTLOC: Sent location: " + locationName);
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
