package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.Translator;

public class DataServer extends Thread {
	private final BlockingQueue<Data> dq;
	private ServerSocket dSockSvr = null;
	public static final int PORT = 2000;

	private static final Logger log = Logger.getLogger(DataServer.class
			.getName());

	public DataServer(BlockingQueue<Data> _dq) {

		dq = _dq;
		if (dq == null) {
			log.warn("Blocking Queue is null!");
		}

		try {
			dSockSvr = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			log.fatal("Cannot open server port " + PORT);
		}

	}

	@Override
	public void run() {

		while (true) {

			Socket dSock = null;
			try {
				dSock = dSockSvr.accept();
				log.info("Link established.");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new DataInputStream(dSock.getInputStream())));
				String line = null;
				Data data = null;
				while ((line = br.readLine()) != null) {
					log.trace("Received data from client: " + line);
					if (dq != null) {
						data = Translator.translate(line+"\n");
						if (data != null) {
							dq.add(data);
						} else {
							log.trace("Unknow data: " + line);
						}
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
