package com.astar.i2r.ins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.Translator;

public class TXTReader implements Runnable {

	private BlockingQueue<Data> dataQ = null;
	private File file = null;

	public TXTReader(File _file, BlockingQueue<Data> _dataQ) {
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
				dataQ.add(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
