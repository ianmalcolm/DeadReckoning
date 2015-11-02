package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public class DataServer extends Thread {
	private final BlockingQueue<String> dq;
	private ServerSocket dSockSvr = null;
	public static final int PORT = 2000;

	private static final Logger log = Logger.getLogger(DataServer.class
			.getName());

	public DataServer(BlockingQueue<String> _dq) {

		dq = _dq;
		if (dq == null) {
			log.warning("Blocking Queue is null!");
		}

		try {
			dSockSvr = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Cannot open server port " + PORT);
		}

	}

	@Override
	public void run() {

		while (true) {

			Socket dSock = null;
			try {
				dSock = dSockSvr.accept();
//				log.info("Link established.");
				// DataInputStream dis = new
				// DataInputStream(dSock.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new DataInputStream(dSock.getInputStream())));
				String clientSentence = null;
				
				while ((clientSentence = br.readLine()) != null) {

					log.fine(clientSentence);
//					System.out.println(log.getName());
//					System.out.println(log.getParent().getName());
	
					if (dq != null) {
						dq.add(clientSentence);
					}
				}
				
				// BufferedReader br = new BufferedReader(new InputStreamReader(
				// dSock.getInputStream()));
				// String clientSentence = null;
				// while (true) {
				// clientSentence = IOUtils.toString(dSock.getInputStream());
				// log.fine(clientSentence);
				// if (dq != null) {
				// dq.add(clientSentence);
				// }
				// }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
