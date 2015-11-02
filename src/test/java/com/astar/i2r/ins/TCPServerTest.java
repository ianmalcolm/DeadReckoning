package com.astar.i2r.ins;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TCPServerTest {

	private static final Logger log = Logger.getLogger("");
	private static Thread dSvr = null;
	private static Thread rSvr = null;

	static {
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.ALL);
		ch.setFormatter(new SimpleFormatter());
		log.addHandler(ch);
		log.setLevel(Level.ALL);
	}

	@BeforeClass
	public static void start() {

		dSvr = new DataServer(null);
		dSvr.start();
		rSvr = new RequestServer();
		rSvr.start();
	}

	@AfterClass
	public static void stop() {
		dSvr.interrupt();
		rSvr.interrupt();
	}

	// public static void main(String[] args) {
	// Result result = JUnitCore.runClasses(TCPServerTest.class);
	// for (Failure failure : result.getFailures()) {
	// System.out.println(failure.toString());
	// }
	// System.out.println(result.wasSuccessful());
	// }

	@Test
	public void dSvrTest() {
		String sentence = "Hello";

		try {
			Socket cSock = new Socket("localhost", DataServer.PORT);
			DataOutputStream dos = new DataOutputStream(cSock.getOutputStream());
			IOUtils.write(sentence, dos);
			IOUtils.write(sentence, dos);

			cSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Test
	// public void rSvrTest() {
	// String ans = null;
	//
	// try {
	// Socket cSock = new Socket("localhost", RequestServer.PORT);
	// DataInputStream dis = new DataInputStream(cSock.getInputStream());
	// DataOutputStream dos = new DataOutputStream(cSock.getOutputStream());
	// IOUtils.write(RequestServer.RQTMAP, dos);
	//
	// // dos.writeBytes(RequestServer.RQTMAP + '\n');
	//
	// ans = IOUtils.toString(dis);
	// System.out.println(ans);
	//
	// // IOUtils.write(RequestServer.RQTMAP + '\n', dos);
	// // ans = IOUtils.toString(dis, StandardCharsets.US_ASCII.name());
	// // System.out.println(ans);
	// // IOUtils.write(RequestServer.RQTMAP + '\n', dos);
	// // ans = IOUtils.toString(dis, StandardCharsets.US_ASCII.name());
	// // System.out.println(ans);
	//
	// cSock.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
