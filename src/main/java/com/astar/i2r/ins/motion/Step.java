package com.astar.i2r.ins.motion;

import java.awt.geom.Point2D;
import java.util.Date;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.geotools.referencing.GeodeticCalculator;

import com.graphhopper.util.GPXEntry;

public class Step extends Vector3D {

	public static final double MINSTEP = 5;

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

	public GPXEntry getDestinationFrom(GPXEntry v) {

		// assert time.after(v.time);

		double x = getX();
		double y = getY();
		double z = getZ();
		Vector3D mirror = new Vector3D(y, x, z);

		// Velocity newv = new Velocity(
		// new Vector3D(v.getY(), v.getX(), v.getZ()), v.time.getTime());
		// reference:
		// http://stackoverflow.com/questions/3917340/geotools-how-to-do-dead-reckoning-and-course-calculations-using-geotools-class

		GeodeticCalculator calc = new GeodeticCalculator();
		// It's odd! setStartingGeographicPoint accept longitude first
		calc.setStartingGeographicPoint(v.lon, v.lat);
		// calc.setDirection(FastMath.toDegrees(Math.PI / 2 - v.getAzimuth()),
		// v.getHorizontalSpeed() * diff);
		calc.setDirection(FastMath.toDegrees(mirror.getAlpha()), getNorm());
		Point2D p = calc.getDestinationGeographicPoint();
		// It's odd! getDestinationGeographicPoint returns longitude first

		return new GPXEntry(p.getY(), p.getX(), getZ(), time.getTime());
	}
}
