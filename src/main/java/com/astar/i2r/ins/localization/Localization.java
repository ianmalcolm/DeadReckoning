package com.astar.i2r.ins.localization;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.motion.GeoPoint;

public class Localization extends Thread {

	private static final Logger log = Logger.getLogger(Localization.class
			.getName());

	private BlockingQueue<Data> dataQ = null;
	private BlockingQueue<GeoPoint> GPSQ = null;
	private BlockingQueue<GeoPoint> DRQ = null;
	private Context car = new Vehicle();

	public Localization(BlockingQueue<Data> _dataQ,
			BlockingQueue<GeoPoint> _GPSQ, BlockingQueue<GeoPoint> _DRQ) {
		dataQ = _dataQ;
		GPSQ = _GPSQ;
		DRQ = _DRQ;

		car.state(NavigationState.DR);
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
				if (car.state() == NavigationState.GPS)
					GPSQ.add(car.getGPS());
				if (car.state() == NavigationState.DR)
					DRQ.add(car.getGPS());
			}

		}
	}
}
