package com.astar.i2r.ins.localization;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.gui.ColoredParticle;
import com.astar.i2r.ins.gui.ColoredWeightedWaypoint;
import com.astar.i2r.ins.motion.GeoPoint;

public class Localization extends Thread {

	private static final Logger log = Logger.getLogger(Localization.class
			.getName());

	public static boolean BOUNDARYFLAG = false;
	public static boolean CORRECTIONFLAG = false;
	public static boolean GROUNDTRUTHFLAG = false;

	private BlockingQueue<Data> dataQ = null;
	private BlockingQueue<ColoredWeightedWaypoint> GPSQ = null;
	private Context car = new Vehicle();

	public Localization(BlockingQueue<Data> _dataQ,
			BlockingQueue<ColoredWeightedWaypoint> _GPSQ) {
		dataQ = _dataQ;
		GPSQ = _GPSQ;
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
				if (car.state() == NavigationState.GPS) {
					GPSQ.add(new ColoredParticle(coordinate.lat,
							coordinate.lon, 0.5, Color.RED));
				} else if (car.state() == NavigationState.DR) {
					GPSQ.add(new ColoredParticle(coordinate.lat,
							coordinate.lon, 0.5, Color.BLUE));
				}
			}

		}
	}
}
