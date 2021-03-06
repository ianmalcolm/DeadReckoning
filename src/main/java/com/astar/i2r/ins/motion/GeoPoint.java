package com.astar.i2r.ins.motion;

import java.awt.geom.Point2D;
import java.util.Date;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.geotools.referencing.GeodeticCalculator;
import org.mapsforge.core.model.LatLong;

import com.graphhopper.util.GPXEntry;

public class GeoPoint {

	// private final LatLong latlong;
	public final double lat;
	public final double lon;
	public final double ele;
	public final Date time = new Date();

	public GeoPoint(double _lat, double _lon, double _ele, long _time) {
		lat = _lat;
		lon = _lon;
		ele = _ele;
		time.setTime(_time);
	}

	public GeoPoint(double _lat, double _lon, double _ele, Date _time) {
		this(_lat, _lon, _ele, _time.getTime());

	}

	public GeoPoint(LatLong _latlong, long _time) {
		this(_latlong.latitude, _latlong.longitude, 0, _time);
	}

	public GeoPoint(double _lat, double _lon, double _ele) {
		this(_lat, _lon, _ele, 0);
	}

	public GeoPoint increment(Velocity v) {

		assert v.time.after(time);

		double diff = (double) (v.time.getTime() - time.getTime()) / 1000;

		// System.out.println(FastMath.toDegrees(v.getAzimuth())+"\t"+v.getHorizontalSpeed());

		Velocity newv = new Velocity(
				new Vector3D(v.getY(), v.getX(), v.getZ()), v.time.getTime());
		// reference:
		// http://stackoverflow.com/questions/3917340/geotools-how-to-do-dead-reckoning-and-course-calculations-using-geotools-class

		GeodeticCalculator calc = new GeodeticCalculator();
		// It's odd! setStartingGeographicPoint accept longitude first
		calc.setStartingGeographicPoint(lon, lat);
		// calc.setDirection(FastMath.toDegrees(Math.PI / 2 - v.getAzimuth()),
		// v.getHorizontalSpeed() * diff);
		calc.setDirection(FastMath.toDegrees(newv.getAlpha()),
				newv.getHorizontalSpeed() * diff);
		Point2D p = calc.getDestinationGeographicPoint();
		// It's odd! getDestinationGeographicPoint returns longitude first
		double newelevation = ele + newv.getVerticalSpeed() * diff;

		return new GeoPoint(p.getY(), p.getX(), 0, newv.time.getTime());
	}

	public static Vector3D distance(GeoPoint p, GeoPoint q) {

		GeodeticCalculator calc = new GeodeticCalculator();

		calc.setStartingGeographicPoint(p.lon, p.lat);
		calc.setDestinationGeographicPoint(q.lon, q.lat);
		double dist = calc.getOrthodromicDistance();
		double alpha = calc.getAzimuth(); // The azimuth, in decimal degrees
											// from -180° to +180°.

		Rotation r = new Rotation(RotationOrder.ZXZ, 0, 0,
				-FastMath.toRadians(alpha));
		Vector3D trip = r.applyTo(new Vector3D(0, 1, 0)).scalarMultiply(dist);

		trip = new Vector3D(trip.getX(), trip.getY(), q.ele - p.ele);

		// in meters
		return trip;
	}

	public GPXEntry toGPXEntry() {
		return new GPXEntry(lat, lon, ele, time.getTime());
	}

	public GeoPoint add(Step step) {

		Step mirror = new Step(step.getY(), step.getX(), step.getZ(),
				step.time.getTime());
		GeodeticCalculator calc = new GeodeticCalculator();

		calc.setStartingGeographicPoint(lon, lat);
		calc.setDirection(FastMath.toDegrees(mirror.getAlpha()),
				mirror.getNorm());

		Point2D p = calc.getDestinationGeographicPoint();

		double newlat = p.getY();
		double newlon = p.getX();
		double newele = ele + step.getZ();

		return new GeoPoint(newlat, newlon, newele, step.time.getTime());

	}

	public String toString() {
		return lat + "," + lon + "," + ele;
	}

}
