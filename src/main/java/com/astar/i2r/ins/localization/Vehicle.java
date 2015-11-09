package com.astar.i2r.ins.localization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.BaroData;
import com.astar.i2r.ins.data.CANData;
import com.astar.i2r.ins.data.CompassData;
import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.MotionData;
import com.astar.i2r.ins.map.GeoMap;
import com.astar.i2r.ins.motion.Attitude;
import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Speed;
import com.astar.i2r.ins.motion.Step;
import com.astar.i2r.ins.motion.Velocity;

class Vehicle implements Context {

	private static final Logger log = Logger.getLogger(Vehicle.class.getName());

	private static double GPSCALIBACCURACYTHRESHOLD = 10;
	private static double GPSCALIBDISTANCETHRESHOLD = 6.66;

	// private static double GPSCALIBACCURACYTHRESHOLD = 15;
	// private static double GPSCALIBDISTANCETHRESHOLD = 4;

	private static double GPSOKTHRESHOLD = 15;

	private Attitude attitude = null;
	private Speed speed;
	private State state = NavigationState.SLAM;
	private Data curData = null;
	private GeoPoint curPos = null;
	private GeoPoint lastAccGPS = null;
	private double lastCompassHeading = 0;
	private Vector3D GPSCalibVector = null;
	private double yawCalib = 2.618;
	private boolean GPSOK = false;
	private Step step = null;
	private List<GeoPoint> steps = new ArrayList<GeoPoint>();
	private GeoMap map = null;
	private double curBaro = 0;
	private double lastGPSOKBaro = 0;
	private boolean GPSCalibrated = false;

	public Vehicle(GeoMap _m) {
		map = _m;
	}

	@Override
	public State state() {
		return state;
	}

	@Override
	public void incoming(Data data) {
		curData = data;
	}

	// set the next state of a vehicle
	@Override
	public void state(State _state) {
		state = _state;
		log.info("Switch into " + state.name() + " at "
				+ new Date(curData.time).toString());
	}

	// calibrate the attitude of a vehicle
	@Override
	public boolean calibrate() {

		if (curData instanceof GPSData) {
			GPSData data = ((GPSData) curData);
			if (isGPSAccurate(data)) {

				GeoPoint curGPS = new GeoPoint(data.gps[0], data.gps[1], 0,
						data.time);

				if (lastAccGPS != null) {
					Vector3D v = GeoPoint.distance(lastAccGPS, curGPS);
					if (v.getNorm() > GPSCALIBDISTANCETHRESHOLD) {
						GPSCalibVector = v;
					} else {
						GPSCalibVector = null;
					}
				} else {
					GPSCalibVector = null;
				}
				lastAccGPS = curGPS;
			} else {
				GPSCalibVector = null;
				lastAccGPS = null;
			}
		} else if (curData instanceof CompassData) {
			CompassData data = ((CompassData) curData);
			lastCompassHeading = data.rotationHeading;
		}

		if (curData instanceof MotionData) {
			MotionData data = ((MotionData) curData);
			// if (GPSCalibrated == false && !Double.isNaN(lastCompassHeading)
			// && false) {
			// double newCalib = getCompassCalibFactor(lastCompassHeading,
			// data.cardan[2]);
			// yawCalib = average(yawCalib, newCalib, 3.0 / 4);
			// lastCompassHeading = Double.NaN;
			// log.debug("Compass Heading Calibration Factor " + newCalib
			// / Math.PI * 180 + "\t Averaged: " + yawCalib / Math.PI
			// * 180);
			// } else
			if (GPSCalibVector != null) {

				double newCalib = getGPSCalibFactor(GPSCalibVector,
						data.cardan[2]);

				yawCalib = average(yawCalib, newCalib, 3.0 / 4);
				GPSCalibrated = true;
				GPSCalibVector = null;
				log.debug("GPS Heading Calibration Factor " + newCalib
						/ Math.PI * 180 + "\t Averaged: " + yawCalib / Math.PI
						* 180);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isGPSOK() {
		if (curData instanceof GPSData) {
			GPSData data = ((GPSData) curData);

			double accuracy = 0;
			accuracy += data.accuracy[0] * data.accuracy[0];
			accuracy += data.accuracy[1] * data.accuracy[1];
			accuracy = Math.sqrt(accuracy);
			if (accuracy > GPSOKTHRESHOLD) {
				if (GPSOK = true) {
					lastGPSOKBaro = curBaro;
				}
				GPSOK = false;
			} else {
				GPSOK = true;
			}
		}
		return GPSOK;
	}

	@Override
	public boolean step() {
		if (curPos == null) {
			return false;
		}
		if (step == null) {
			step = new Step(0, 0, 0, curData.time);
		}

		if (curData instanceof CANData) {
			step(((CANData) curData));
		} else if (curData instanceof MotionData) {
			step(((MotionData) curData));
		} else if (curData instanceof CompassData) {
			CompassData data = ((CompassData) curData);
			lastCompassHeading = data.rotationHeading;
		} else if (curData instanceof BaroData) {
			BaroData data = ((BaroData) curData);
			curBaro = data.altitude;
		}

		if (step.getNorm() > Step.MINSTEP) {
			return true;
		} else {
			return false;
		}
	}

	private void step(CANData data) {
		speed = new Speed(data.vehSpd, data.time);
		Vector3D vel = attitude.getVelocity(speed.speed);
		Velocity approxVel = new Velocity(vel, data.time);
		step = step.increment(approxVel);
	}

	private void step(MotionData data) {

		// if (GPSCalibrated == false && !Double.isNaN(lastCompassHeading)
		// && false) {
		// double newCalib = getCompassCalibFactor(lastCompassHeading,
		// data.cardan[2]);
		// yawCalib = average(yawCalib, newCalib, 3.0 / 4);
		// lastCompassHeading = Double.NaN;
		// log.debug("Compass Heading Calibration Factor " + newCalib
		// / Math.PI * 180 + "\t Averaged: " + yawCalib / Math.PI
		// * 180);
		// }

		attitude = new Attitude(data.cardan, yawCalib, data.time);
		Vector3D vel = attitude.getVelocity(speed.speed);
		Velocity approxVel = new Velocity(vel, data.time);
		step = step.increment(approxVel);
	}

	@Override
	public boolean localize() {

		// GeoPosition calibPos = matcher.localize(steps);
		//
		// if (calibPos != null) {
		// curPos = calibPos;
		// steps.clear();
		// steps.add(curPos);
		//
		// return true;
		// } else {
		//
		// return false;
		// }
		return false;
	}

	private boolean isGPSAccurate(GPSData data) {
		double accuracy = 0;
		accuracy += data.accuracy[0] * data.accuracy[0];
		accuracy += data.accuracy[1] * data.accuracy[1];
		accuracy = Math.sqrt(accuracy);
		if (accuracy > GPSCALIBACCURACYTHRESHOLD) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean GPSUpdate() {
		if (curPos == null) {
			curPos = new GeoPoint(0, 0, 0, curData.time);
		}

		if (curData instanceof BaroData) {
			BaroData data = ((BaroData) curData);
			double lat = curPos.lat;
			double lon = curPos.lon;
			curPos = new GeoPoint(lat, lon, data.altitude, data.time);
			curBaro = data.altitude;
		} else if (curData instanceof GPSData) {
			GPSData data = ((GPSData) curData);
			double ele = curPos.ele;
			curPos = new GeoPoint(data.gps[0], data.gps[1], ele, data.time);
			speed = new Speed(data.speedkmh, data.time);

		}

		step = null;
		return true;
	}

	@Override
	public boolean SLAMUpdate() {
		curPos = curPos.add(step);
//		curPos = new GeoPoint(curPos.lat, curPos.lon, curBaro-lastGPSOKBaro,
//				curPos.time.getTime());
		curPos = new GeoPoint(curPos.lat, curPos.lon, curBaro,
		curPos.time.getTime());
		step = null;
		return true;
	}

	@Override
	public GeoPoint getGPS() {

		return curPos;
	}

	private double average(double a1, double a2, double rate) {

		a1 = a1 % (Math.PI * 2);
		a2 = a2 % (Math.PI * 2);
		if (Math.abs(a1 - a2) > Math.PI) {
			if (a1 > a2) {
				a2 = a2 + (Math.PI * 2);
			} else {
				a1 = a1 + (Math.PI * 2);
			}
		}

		double a = a1 * rate + a2 * (1 - rate);

		return a;
	}

	/**
	 * 
	 * @param gpsyaw
	 * @param attyaw
	 * @return
	 */
	private double getGPSCalibFactor(Vector3D v, double attyaw) {
		double gpsyaw = v.getAlpha() + Math.PI / 2;
		double newCalib = (gpsyaw - attyaw) % (Math.PI * 2);
		return newCalib;

	}

	/**
	 * 
	 * @param comheading
	 *            degree
	 * @param attyaw
	 *            radian
	 * @return calibration factor for attitude
	 */
	private double getCompassCalibFactor(double comheading, double attyaw) {
		double comCalib = 3.479938117 - 1.85;
		double comyaw = (-comheading) / 180 * Math.PI;
		comyaw = comyaw + comCalib;
		// comyaw = comyaw % (Math.PI * 2);
		double calib = (comyaw - attyaw);
		// calib = calib % (Math.PI * 2);

		return calib;

	}

}