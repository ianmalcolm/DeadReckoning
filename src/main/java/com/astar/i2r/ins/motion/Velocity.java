package com.astar.i2r.ins.motion;

import java.util.Date;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Velocity extends Vector3D {

	Date time = new Date();

	public Velocity(double[] in, long _time) throws DimensionMismatchException {
		super(in);
		time.setTime(_time);
	}

	public Velocity(Vector3D in, long _time) {
		super(in.getX(), in.getY(), in.getZ());
		time.setTime(_time);
	}

	public Velocity increment(Accelerate acc) {
		assert acc.time.after(time);
		double diff = (double) (acc.time.getTime() - time.getTime()) / 1000.0;
		return new Velocity(add(acc.getIncrement(diff)), acc.time.getTime());
	}

	public double getHorizontalSpeed() {
		return new Vector2D(getX(), getY()).getNorm();
	}

	public double getVerticalSpeed() {
		return getZ();
	}

	public Velocity calibrate(double speed) {
		return new Velocity(scalarMultiply(1 / getNorm() * speed),
				time.getTime());
	}



	public double getSpeed() {
		return getNorm();
	}
}
