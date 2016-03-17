package com.astar.i2r.ins.localization;

import java.awt.Color;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.gui.ColoredParticle;
import com.astar.i2r.ins.gui.ColoredWeightedWaypoint;
import com.astar.i2r.ins.map.Map;
import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Step;

public class Localization extends Thread {

	private static final Logger log = Logger.getLogger(Localization.class
			.getName());

	public static boolean BOUNDARYFLAG = false;
	public static boolean CORRECTIONFLAG = false;
	public static boolean GROUNDTRUTHFLAG = false;
	public static boolean SNAPPEDGPS = false;
	public static boolean VERBOSE = false;

	private BlockingQueue<Data> dataQ = null;
	private BlockingQueue<ColoredWeightedWaypoint> GPSQ = null;
	private Context car = new Vehicle();
	private static Map map = null;

	public Localization(BlockingQueue<Data> _dataQ,
			BlockingQueue<ColoredWeightedWaypoint> _GPSQ, Map _map) {
		dataQ = _dataQ;
		GPSQ = _GPSQ;
		map = _map;
		car.state(NavigationState.DR);
	}

	@Override
	public void run() {
		GeoPoint lastGPS = null;
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
				if (VERBOSE) {
					if (lastGPS == null) {
						lastGPS = coordinate;
						System.out.println("Time: " + coordinate.time.getTime()
								+ "\tGPS coordinate: " + coordinate.toString());
					} else {
						double dist = GeoPoint.distance(lastGPS, coordinate)
								.getNorm();
						if (dist > Step.MINSTEP) {
							lastGPS = coordinate;
							System.out.println("Time: "
									+ coordinate.time.getTime()
									+ "\tGPS coordinate: "
									+ coordinate.toString());
						}
					}

				}
			}

		}
	}

	public static GeoPoint findClosestSnappedPoint(GeoPoint gp) {
		if (Localization.SNAPPEDGPS) {
			GeoPoint snappedGP = map.findClosestSnappedPoint(gp);
			return snappedGP;
		} else {
			return null;
		}
	}
}
