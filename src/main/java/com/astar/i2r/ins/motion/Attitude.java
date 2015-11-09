package com.astar.i2r.ins.motion;

import java.util.Date;
import java.util.logging.Logger;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.util.FastMath;

public class Attitude extends Rotation {

	private static final Logger log = Logger
			.getLogger(Attitude.class.getName());

	public Date time = new Date();

	// suppose when cardan(0,0,0), the vehicle heads to north, i.e. v(0,1,0)
	// This assumption needs to be calibrate
	public static final Vector3D ZEROPOINTVECTOR = new Vector3D(0, -1, 0);
	public static final Vector3D HEADING = new Vector3D(0, 0, -1);

	/**
	 * set the attitude with Cardan angles, the input Cardan angles are relate
	 * to a fixed frame that does not vary with the time, e.g. an initial
	 * attitude of the device.
	 * 
	 * @param pitch
	 *            A pitch is a rotation around a lateral axis that passes
	 *            through the device from side to side.
	 * @param roll
	 *            A roll is a rotation around a longitudinal axis that passes
	 *            through the device from its top to bottom.
	 *
	 * @param yaw
	 *            A yaw is a rotation around an axis that runs vertically
	 *            through the device. It is perpendicular to the body of the
	 *            device, with its origin at the center of gravity and directed
	 *            toward the bottom of the device.
	 */
	public Attitude(double[] in, long _time) {
		this(in[1], in[0], in[2], _time);
	}

	public Attitude(double[] in, double calib, long _time) {
		this(in[1], in[0], in[2] + calib, _time);
	}

	public Attitude(double roll, double pitch, double yaw, long _time) {
		// Extrinsic to intrinsic conversion
		// rotation in euler ZXZ style
		super(RotationOrder.ZXZ, yaw, pitch, roll);
		time.setTime(_time);
	}

	Attitude(double q0, double q1, double q2, double q3, long _time) {
		super(q0, q1, q2, q3, false);
		time.setTime(_time);
	}

	/**
	 * Transform the acceleration from the coordinates of device to that of
	 * world The coordinates of the world: x,y,z are corresponding to the the
	 * lines of latitude, longitude and altitude
	 * 
	 * @param acc
	 *            The acceleration with respect to the device
	 * @return The acceleration with respect to the world.
	 */

	// public Vector3D device2World(double[] accIn) {
	// Vector3D in = new Vector3D(accIn);
	// return att.applyTo(in);
	// }

	public Vector3D device2World(Vector3D in) {
		return applyTo(in);
	}

	// public void set(double[] in, long _time) {
	// log.fine("Input attitude:\t" + in[0] + "\t" + in[1] + "\t" + in[2]);
	// att = new Rotation(RotationOrder.XYZ, in[0], in[1], in[2]);
	// time.setTime(_time);
	// }

	// public void calibrate(Velocity v) {
	// // take time into consideration
	//
	// // project velocity to the heading vector
	// Vector3D attVector = att.applyTo(ZEROPOINTVECTOR);
	// ArrayRealVector I = new ArrayRealVector(attVector.toArray());
	// ArrayRealVector V = new ArrayRealVector(v.toArray());
	// ArrayRealVector pV = (ArrayRealVector) V.projection(I);
	// v.set(pV.toArray(), v.time.getTime());
	// }

	public Vector3D getVelocity(double in) {
		Vector3D attVector = applyTo(HEADING);
		return attVector.scalarMultiply(in);
	}

	// public Attitude calibrate(Rotation r) {
	// Rotation calRot = applyTo(r);
	// Attitude newAtt = new Attitude(calRot.getQ0(), calRot.getQ1(),
	// calRot.getQ2(), calRot.getQ3(), time.getTime());
	// // return this;
	// return newAtt;
	// }

	// /**
	// * Update the current attitude with a relative Cardan angles, e.g. relate
	// to
	// * the last Cardan angles measurements
	// *
	// * @param roll
	// * @param pitch
	// * @param yaw
	// */
	// void increment(double roll, double pitch, double yaw) {
	//
	// }
	//
	// /**
	// * Set the attitude with Cardan angles
	// *
	// * @param roll
	// * @param pitch
	// * @param yaw
	// */
	// void set(double roll, double pitch, double yaw) {
	//
	// }

	public String toString() {
		return applyTo(HEADING).toString();
	}

}
