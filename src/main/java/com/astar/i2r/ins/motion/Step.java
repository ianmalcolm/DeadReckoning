package com.astar.i2r.ins.motion;

import java.awt.geom.Point2D;
import java.util.Date;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.geotools.referencing.GeodeticCalculator;

public class Step extends Vector3D {

	public static final double MINSTEP = 1;

	Date time = new Date();

	public Step(double[] in, long _time) {
		super(in);
		time.setTime(_time);
	}

	public Step(Vector3D in, long _time) {
		super(in.toArray());
		time.setTime(_time);
	}

	public Step(double x, double y, double z, long _time) {
		super(x, y, z);
		time.setTime(_time);
	}

	public Step add(Step increment) {
		return new Step(add(increment).toArray(), increment.time.getTime());
	}

	public Step increment(Velocity v) {
		double diff = (v.time.getTime() - time.getTime()) / 1000.0;
		return new Step(add(v.scalarMultiply(diff)), v.time.getTime());
	}

}
