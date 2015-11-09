package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TCPServerTest {

	private static final Logger log = Logger.getLogger("");
	private static Thread dSvr = null;
	private static Thread rSvr = null;

	static {
//		BasicConfigurator.configure(); // enough for configuring log4j
		log.setLevel(Level.OFF);

	}

	@BeforeClass
	public static void start() {

		dSvr = new DataServer(null);
		dSvr.start();
		rSvr = new RequestServer(null,null);
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

	@Test
	public void rSvrTest() {
		String ans = null;

		try {
			Socket cSock = new Socket("localhost", RequestServer.PORT);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new DataInputStream(cSock.getInputStream())));

			DataOutputStream dos = new DataOutputStream(cSock.getOutputStream());

			{
				IOUtils.write(RequestServer.RQTGPS + '\n', dos);
				System.out.println("Client sent request: "
						+ RequestServer.RQTGPS);
				ans = br.readLine();
				System.out.println("Client received answer: " + ans);
			}

			dos.flush();
			dos.close();
			br.close();
			cSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Socket cSock = new Socket("localhost", RequestServer.PORT);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new DataInputStream(cSock.getInputStream())));

			DataOutputStream dos = new DataOutputStream(cSock.getOutputStream());

			{
				IOUtils.write(RequestServer.RQTMAP + '\n', dos);
				System.out.println("Client sent request: "
						+ RequestServer.RQTMAP);

				DataInputStream dis = new DataInputStream(
						cSock.getInputStream());
				String filename = dis.readUTF();
				FileOutputStream fos = new FileOutputStream("new" + filename);
				// BufferedOutputStream bos = new BufferedOutputStream(fos);
				int bytes = IOUtils.copy(dis, fos);

				// bos.close();
				dos.flush();
				dos.close();
				br.close();
				cSock.close();
				System.out.println("Client received file size: " + bytes);

			}

			cSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Socket cSock = new Socket("localhost", RequestServer.PORT);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new DataInputStream(cSock.getInputStream())));

			DataOutputStream dos = new DataOutputStream(cSock.getOutputStream());

			{
				IOUtils.write(RequestServer.RQTLOC + '\n', dos);
				System.out.println("Client sent request: "
						+ RequestServer.RQTLOC);
				ans = br.readLine();
				System.out.println("Client received answer: " + ans);
			}
			dos.flush();
			dos.close();
			br.close();
			cSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
