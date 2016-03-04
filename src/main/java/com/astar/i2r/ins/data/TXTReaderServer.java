package com.astar.i2r.ins.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

public class TXTReaderServer extends Thread {

	private BlockingQueue<Data> dataQ = null;
	private File file = null;
	private static final Logger log = Logger.getLogger(TXTReaderServer.class
			.getName());
	public static int DELAY = 0;

	public TXTReaderServer(File _file, BlockingQueue<Data> _dataQ) {
		file = _file;
		dataQ = _dataQ;
	}

	@Override
	public void run() {

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				Data data = Translator.translate(line);
				if (data == null) {
					log.trace("Cannot translate " + line);
					continue;
				}
				dataQ.add(data);
				if (DELAY > 0) {
					Thread.sleep(DELAY);
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;

	}

}
