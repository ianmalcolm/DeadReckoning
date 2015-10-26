package com.astar.i2r.ins.motion;

import java.util.Date;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Accelerate {
	Vector3D acc = null;
	Date time = new Date();

	/*
	 * Initialization of an acceleration
	 */
	public Accelerate(double[] in, long _time) {
		acc = new Vector3D(in);
		time.setTime(_time);
	}

	// public void set(double[] in, long _time) {
	// acc = new Vector3D(in);
	// time.setTime(_time);
	// }

	public void set(Vector3D in, long _time) {
		acc = in;
		time.setTime(_time);
	}

	public Vector3D getIncrement(double period) {
		return acc.scalarMultiply(period);
	}

	/**
	 * Get the acceleration in the form of array
	 * 
	 * @return
	 */
	public double[] toArray() {
		return acc.toArray();
	}

}
