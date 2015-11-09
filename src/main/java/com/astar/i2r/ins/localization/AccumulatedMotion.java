package com.astar.i2r.ins.localization;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.mapsforge.core.model.LatLong;

import com.astar.i2r.ins.INS;
import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.MagneticData;
import com.astar.i2r.ins.data.MotionData;
import com.astar.i2r.ins.motion.Accelerate;
import com.astar.i2r.ins.motion.Attitude;
import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.MagneticField;
import com.astar.i2r.ins.motion.Velocity;

public class AccumulatedMotion extends ArrayList<GeoPoint> {

	private static final Logger log = Logger.getLogger(AccumulatedMotion.class
			.getName());

	private Attitude attitude = null;
	private Accelerate accelerate = null;
	private Velocity velocity = null;
	private Double speedkmh = Double.NaN;
	private MagneticField magnet = null;

	public void increment(Data data) {

		if ((attitude == null || accelerate == null)
				&& data instanceof MotionData) {
			// initialize attitude and accelerate
			attitude = new Attitude(((MotionData) data).cardan, data.time);
			accelerate = new Accelerate(((MotionData) data).accelerate,
					data.time);
		}

		if (speedkmh.isNaN() && data instanceof GPSData) {
			speedkmh = ((GPSData) data).speedkmh / 3.6;
			if (size() < 1) {
				// initialize position
				double[] p = ((GPSData) data).gps;
				GeoPoint pos = new GeoPoint(new LatLong(p[0], p[1]),
						data.time);
				add(pos);
			}
		}

		if (velocity == null && !speedkmh.isNaN() && attitude != null) {
			// initialize velocity
			velocity = new Velocity(attitude.getVelocity(speedkmh),
					attitude.time.getTime());
		}

		if (magnet == null && data instanceof MagneticData) {
			MagneticData mData = ((MagneticData) data);
			magnet = new MagneticField(mData.magnetic, mData.time);
		}

		if (attitude != null && accelerate != null && velocity != null
				&& size() > 0 && magnet != null) {
			if (data instanceof MotionData) {
				increment((MotionData) data);
			} else if (data instanceof GPSData) {
				increment((GPSData) data);

			} else if (data instanceof MagneticData) {
				increment((MagneticData) data);
			}
		}

	}

	private void increment(MotionData data) {

		// calculate the z angle with respect to north
		double heading = 0.0;

		{
			double x = magnet.getX();
			double y = magnet.getY();
			double z = magnet.getZ();

			if (y > 0)
				heading = 90.0 - Math.atan(x / y) * 180.0 / Math.PI;
			if (y < 0)
				heading = 270.0 - Math.atan(x / y) * 180.0 / Math.PI;
			if (y == 0 && x < 0)
				heading = 180.0;
			if (y == 0 && x > 0)
				heading = 0.0;
		}

		// update attitude
		double[] combinedCardan = { data.cardan[0], data.cardan[1],
				FastMath.toRadians(heading) };

		 attitude = new Attitude(data.cardan, data.time);
//		attitude = new Attitude(combinedCardan, data.time);

		// double roll = FastMath.toDegrees(data.cardan[0]);
		// double pitch = FastMath.toDegrees(data.cardan[1]);
		// double yaw = FastMath.toDegrees(data.cardan[2]);
		// System.out.println(new Vector3D(roll, pitch, yaw).toString());

		// update accelerate
		// Vector3D worldAcc = attitude.device2World(data.accelerate);

		// accelerate.set(worldAcc, data.time);

		// update velocity
		// velocity.increment(accelerate);

		Vector3D vel = attitude.getVelocity(speedkmh);
		Velocity approxVel = new Velocity(vel, data.time);
		// System.out.println(approxVel.toString());

		// System.out.println("Time:\t" + approxVel.time.getTime()
		// + "\tapproxV:\t" + approxVel.toString());

		// update position
		GeoPoint newPosition = get(size() - 1).increment(approxVel);
		// Position newPosition = get(size() - 1).increment(velocity);

		add(newPosition);

	}

	private void increment(GPSData data) {
		speedkmh = data.speedkmh;
		// Vector2D gpsAcc = new Vector2D(data.accuracy);
		// if (gpsAcc.getNorm() <= 6) {
		// if (data.speedkmh >= 10) {
		// velocity.calibrate(data.speedkmh / 3.6);
		// }
		// }
		
		double heading = 0.0;

		{
			double x = magnet.getX();
			double y = magnet.getY();
			double z = magnet.getZ();

			if (y > 0)
				heading = 90.0 - Math.atan(x / y) * 180.0 / Math.PI;
			if (y < 0)
				heading = 270.0 - Math.atan(x / y) * 180.0 / Math.PI;
			if (y == 0 && x < 0)
				heading = 180.0;
			if (y == 0 && x > 0)
				heading = 0.0;
		}
		
		System.out.println(heading);
	}

	private void increment(MagneticData data) {
		magnet = new MagneticField(data.magnetic, data.time);
	}

	public String currentPosition() {
		if (size() < 1) {
			return "Position not defined!";
		} else {
			String t = get(size() - 1).time.toString();
			GeoPoint lastPosition = get(size() - 1);
			LatLong curPos = new LatLong(lastPosition.lat, lastPosition.lon);
			return t + "\t" + curPos.toString();
		}
	}
}
