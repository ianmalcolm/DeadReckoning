package com.astar.i2r.ins.motion;

import java.util.Date;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Velocity {

	private Vector3D v = null;
	Date time = new Date();

	public Velocity(double[] in, long _time) throws DimensionMismatchException {
		v = new Vector3D(in);
		time.setTime(_time);
	}

	public Velocity(Vector3D in, long _time) {
		v = in;
		time.setTime(_time);
	}

	public void set(double[] in, long _time) throws DimensionMismatchException {
		v = new Vector3D(in);
		time.setTime(_time);
	}

	public void increment(Accelerate acc) {
		assert acc.time.after(time);

		double diff = (double) (acc.time.getTime() - time.getTime()) / 1000.0;
		v = v.add(acc.getIncrement(diff));
		time.setTime(acc.time.getTime());
	}

	public double[] toArray() {
		return v.toArray();
	}

	public double getAzimuth() {
		return v.getAlpha();
	}

	public double getHorizontalSpeed() {
		return new Vector2D(v.getX(), v.getY()).getNorm();
	}

	public double getVerticalSpeed() {
		return v.getZ();
	}

	public void calibrate(double speed) {
		v.scalarMultiply(1 / v.getNorm() * speed);
	}
	
	public String toString(){
		return v.toString();
	}
	
	public double getSpeed(){
		return v.getNorm();
	}
}
