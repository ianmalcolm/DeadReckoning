package com.astar.i2r.ins.localization;

import java.util.Date;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.Logger;

import com.astar.i2r.ins.data.BaroData;
import com.astar.i2r.ins.data.CANData;
import com.astar.i2r.ins.data.CompassData;
import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.GroundTruth;
import com.astar.i2r.ins.data.MotionData;
import com.astar.i2r.ins.map.CarPark;
import com.astar.i2r.ins.map.CarParkDB;
import com.astar.i2r.ins.motion.Attitude;
import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Speed;
import com.astar.i2r.ins.motion.Step;
import com.astar.i2r.ins.motion.Velocity;

class Vehicle implements Context {

	private static final Logger log = Logger.getLogger(Vehicle.class.getName());

	private static double GPSCALIBACCURACYTHRESHOLD = 10;
	private static double GPSCALIBDISTANCETHRESHOLD = 10;

	private static double GPSOKTHRESHOLD = 15;

	private GPSData lastGPSData = null;

	private Attitude attitude = null;
	private Speed speed = null;

	private Data curData = null;
	private GeoPoint curPos = null;
	private Vector3D GPSCalibVector = null;
	// private double yawCalib = Double.NaN;
	// private double yawCalib = 2.32;
	private double yawCalib = 0;
	private Step step = null;
	private double baroAltitude = Double.NaN;
	private double lpBaro = Double.NaN;
	private CarPark curPark = null;
	private State state = NavigationState.DR;

	// set the next state of a vehicle
	@Override
	public void state(State nextState) {

		assert nextState != state;
		state = nextState;

		if (state == NavigationState.DR) {
			step = new Step(0, 0, 0, curData != null ? curData.time : 0);
		} else if (state == NavigationState.GPS) {
			step = null;
		}

		log.info("Switch into " + state.name() + " at "
				+ new Date(curData != null ? curData.time : 0).toString());
	}

	@Override
	public State needStateSwitch() {
		State nextState = null;

		if (state == NavigationState.GPS && curData instanceof GPSData
				&& curPos != null) {
			if (!isGPSOK(((GPSData) curData))) {
				nextState = NavigationState.DR;
			} else if (Localization.BOUNDARYFLAG) {
				curPark = CarParkDB.getCarPark(curPos.lat, curPos.lon);
				if (curPark != null) {
					nextState = NavigationState.DR;
				}
			}
		}

		if (state == NavigationState.DR) {
			if (curPos == null) {
				nextState = NavigationState.GPS;
			} else if (curData instanceof GPSData) {
				if (isGPSOK(((GPSData) curData)) && !Localization.BOUNDARYFLAG) {
					nextState = NavigationState.GPS;
				} else if (isGPSOK(((GPSData) curData)) && Localization.BOUNDARYFLAG) {
					curPark = CarParkDB.getCarPark(curPos.lat, curPos.lon);
					if (curPark == null) {
						nextState = NavigationState.GPS;
					}
				}
			}
		}

		if (nextState == state) {
			return null;
		} else {
			return nextState;
		}
	}

	private boolean isGPSOK(GPSData data) {
		double acc = data.accuracy[0] * data.accuracy[0] + data.accuracy[1]
				+ data.accuracy[1];
		acc = Math.sqrt(acc);
		return acc <= GPSOKTHRESHOLD;
	}

	@Override
	public boolean DRUpdate() {

		if (curData instanceof CANData) {
			DRUpdate(((CANData) curData));
		} else if (curData instanceof MotionData) {
			DRUpdate(((MotionData) curData));
		} else if (curData instanceof CompassData) {
			DRUpdate(((CompassData) curData));
		} else if (curData instanceof BaroData) {
			DRUpdate(((BaroData) curData));
		} else if (curData instanceof GPSData) {
			DRUpdate(((GPSData) curData));
		} else if (curData instanceof GroundTruth) {
			DRUpdate(((GroundTruth) curData));
		}

		return true;
	}

	private void DRUpdate(CANData data) {
		speed = new Speed(data.vehSpdkmh / 3.6, data.time);
	}

	private void DRUpdate(MotionData data) {

		attitude = new Attitude(data.cardan, yawCalib, data.time);

		if (speed == null || curPos == null) {
			return;
		}

		assert step != null;

		Vector3D vel = attitude.getVelocity(speed.speedms);
		Velocity approxVel = new Velocity(vel, data.time);
		step = step.increment(approxVel);

		if (step.getNorm() > Step.MINSTEP) {
			log.trace("Step: " + step.toString() + " GPS: " + curPos.toString());
			curPos = curPos.add(step);
			System.out.println("Time: " + data.time + " GPS: "
					+ curPos.toString());
			step = new Step(0, 0, 0, data.time);

			if (curPark != null && Localization.CORRECTIONFLAG) {
				double correctHeading = curPark.getCorrectHeading(curPos.lat,
						curPos.lon);
				if (!Double.isNaN(correctHeading)) {
					double alpha = attitude.getVelocity(1).getAlpha() + Math.PI
							/ 2;
					double gtcalib = correctHeading + Math.PI - alpha;
					yawCalib += gtcalib;
				}
			}
		}
	}

	private void DRUpdate(CompassData data) {
	}

	private void DRUpdate(GPSData data) {
		// No valid GPS data in DR mode
	}

	private void DRUpdate(BaroData data) {
		baroAltitude = data.altitude;
		if (Double.isNaN(lpBaro)) {
			lpBaro = data.altitude;
		}
	}

	private void DRUpdate(GroundTruth data) {

		if (Localization.GROUNDTRUTHFLAG) {
			double e = 0;
			if (curPos != null) {
				e = curPos.ele;
			}
			curPos = new GeoPoint(data.lat, data.lon, e, data.time);
			if (attitude != null) {
				double alpha = attitude.getVelocity(1).getAlpha() + Math.PI / 2;
				double gtcalib = data.heading + Math.PI - alpha;
				yawCalib += gtcalib;
			}
			speed = new Speed(data.speedms, data.time);
			step = new Step(0, 0, 0, data.time);
		}
	}

	private boolean isGPSAccurate(GPSData data) {
		double acc = data.accuracy[0] * data.accuracy[0] + data.accuracy[1]
				+ data.accuracy[1];
		acc = Math.sqrt(acc);

		return acc <= GPSCALIBACCURACYTHRESHOLD;
	}

	@Override
	public boolean GPSUpdate() {

		if (curData instanceof BaroData) {
			GPSUpdate((BaroData) curData);
		} else if (curData instanceof GPSData) {
			GPSUpdate((GPSData) curData);
		} else if (curData instanceof CANData) {
			GPSUpdate((CANData) curData);
		} else if (curData instanceof MotionData) {
			GPSUpdate((MotionData) curData);
		} else if (curData instanceof CompassData) {
			GPSUpdate((CompassData) curData);
		} else if (curData instanceof GroundTruth) {
			GPSUpdate((GroundTruth) curData);
		}

		return true;
	}

	private void GPSUpdate(BaroData data) {
		baroAltitude = data.altitude;
		lpBaro = baroAltitude;
	}

	private void GPSUpdate(CANData data) {

	}

	private void GPSUpdate(MotionData data) {

		if (GPSCalibVector != null) {
			double newCalib = getGPSCalibFactor(GPSCalibVector, data.cardan[2]);

			if (yawCalib == 0) {
				yawCalib = newCalib;
			} else {
				yawCalib = angleAverage(yawCalib, newCalib, 3.0 / 4);
			}

			GPSCalibVector = null;
			log.debug("GPS Heading Calibration Factor " + newCalib / Math.PI
					* 180 + "\t Averaged: " + yawCalib / Math.PI * 180);
		}

		attitude = new Attitude(data.cardan, yawCalib, data.time);
	}

	private void GPSUpdate(GPSData data) {

		curPos = new GeoPoint(data.gps[0], data.gps[1], 0, data.time);
		speed = new Speed(data.speedms, data.time);

		if (lastGPSData == null) {
			lastGPSData = data;
		} else if (isGPSAccurate(data) && isGPSAccurate(lastGPSData)
				&& lastGPSData != data) {
			// generator calibration factor

			GeoPoint lastGPS = new GeoPoint(lastGPSData.gps[0],
					lastGPSData.gps[1], 0, lastGPSData.time);
			Vector3D v = GeoPoint.distance(lastGPS, curPos);
			if (v.getNorm() > GPSCALIBDISTANCETHRESHOLD) {
				GPSCalibVector = v;
			} else {
				GPSCalibVector = null;
			}
			lastGPSData = data;
		} else {
			GPSCalibVector = null;
		}

	}

	private void GPSUpdate(CompassData data) {

	}

	private void GPSUpdate(GroundTruth data) {

		if (Localization.GROUNDTRUTHFLAG) {
			double e = 0;
			if (curPos != null) {
				e = curPos.ele;
			}
			curPos = new GeoPoint(data.lat, data.lon, e, data.time);
			if (attitude != null) {
				double alpha = attitude.getVelocity(1).getAlpha() + Math.PI / 2;
				double gtcalib = data.heading + Math.PI - alpha;
				yawCalib += gtcalib;
			}
		}
	}

	@Override
	public GeoPoint getGPS() {

		return curPos;
	}

	private double angleAverage(double a1, double a2, double rate) {

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

	@Override
	public double getRelativeAltitude() {
		if (state == NavigationState.GPS) {
			return 0;
		} else if (state == NavigationState.DR) {
			double r = baroAltitude - lpBaro;
			if (Double.isNaN(r))
				return 0;
			else
				return r;
		}
		return 0;
	}

	@Override
	public void process(Data data) {
		curData = data;
		state.process(this);
	}

	public String getMapName() {
		if (curPark == null) {
			return null;
		} else {
			return curPark.getMapName(getRelativeAltitude());
		}
	}

	public double[] getMapParameter() {
		String mapName = getMapName();
		if (mapName != null) {
			return curPark.getMapParameter(mapName);
		} else {
			return null;
		}
	}

	public String getMapFileName() {
		String mapName = getMapName();
		if (mapName != null) {
			return curPark.getMapFileName(mapName);
		} else {
			return null;
		}
	}

	@Override
	public State state() {
		return state;
	}

}
