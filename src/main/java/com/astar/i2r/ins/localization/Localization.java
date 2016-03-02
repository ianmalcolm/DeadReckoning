package com.astar.i2r.ins.localization;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.motion.GeoPoint;

public class Localization implements Runnable {

	private static final Logger log = Logger.getLogger(Localization.class
			.getName());

	private BlockingQueue<Data> dataQ = null;
	private BlockingQueue<GeoPoint> GPSQ = null;
	private Context car = new Vehicle();

	public Localization(BlockingQueue<Data> _dataQ,
			BlockingQueue<GeoPoint> _GPSQ) {
		dataQ = _dataQ;
		GPSQ = _GPSQ;

	}

	@Override
	public void run() {
		while (true) {

			Data data = null;
			try {
				data = dataQ.take();
				log.trace("Receive a " + data.getClass().getSimpleName());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			assert data != null;

			car.process(data);

			GeoPoint coordinate = car.getGPS();
			if (coordinate != null) {
				System.out.println(coordinate.toString());
				// GPSQ.add(car.getGPS());
			}

		}
	}
}
