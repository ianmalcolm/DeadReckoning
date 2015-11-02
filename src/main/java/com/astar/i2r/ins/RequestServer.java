package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.astar.i2r.ins.motion.GeoPosition;

public class RequestServer extends Thread {
	private static final Logger log = Logger.getLogger(RequestServer.class
			.getName());

	public static final String RQTGPS = "RQTGPS";
	public static final String RQTMAP = "RQTGPS";
	public static final String RQTLOC = "RQTGPS";

	private ServerSocket rSockSvr = null;
	public static final int PORT = 2001;

	private String loc = "Clementi Ave 2";
	private GeoPosition gps = new GeoPosition(1.315123, 103.771873, 0, 0);

	public RequestServer() {

		try {
			rSockSvr = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Cannot open server port " + PORT);
		}
	}

	@Override
	public void run() {
		String cmd = "";
		while (true) {
			Socket rSock = null;
			try {
				rSock = rSockSvr.accept();
				log.fine("Link established.");
				cmd = IOUtils.toString(rSock.getInputStream());
				log.info("Received: " + cmd);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] reply = null;

			if (cmd.contains(RQTGPS)) {
				reply = gps.toString().getBytes();
			} else if (cmd.contains(RQTMAP)) {
				reply = "MAP".getBytes();
			} else if (cmd.contains(RQTLOC)) {
				reply = loc.getBytes();
			} else {
				reply = "Invalid request!".getBytes();
				log.info("Received invalid request from client.");
			}

			DataOutputStream dos;

			try {
				dos = new DataOutputStream(rSock.getOutputStream());
				dos.write(reply);
				log.fine("Sent msg to client: " + reply.toString());
				dos.flush();
				rSock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
