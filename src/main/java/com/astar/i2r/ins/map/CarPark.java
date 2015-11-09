package com.astar.i2r.ins.map;

import java.awt.Polygon;
import java.io.File;

import com.astar.i2r.ins.motion.GeoPoint;
import com.graphhopper.util.shapes.GHPlace;

public class CarPark implements CarParkInterface {
	// the first element store the map of the highest altitude
	private double altitude;
	private String filename;
	private String name;
	private double rotation;
	private double scale;
	private GeoPoint reference = new GeoPoint(1.2992444, 103.7875234, 0, 0);
	private Polygon bbox;

	public CarPark(double alt, String fn, String n, double rot, double sc,
			GeoPoint ref, Polygon bb) {
		altitude = alt;
		filename = fn;
		name = n;
		rotation = rot;
		scale = sc;
		reference = ref;
		bbox = bb;
	}

	public static int toInt(double gps) {
		Double d = gps * 1e6;
		return d.intValue();
	}

	@Override
	public boolean isInBBox(double lat, double lon) {
		return bbox.contains(toInt(lat), toInt(lon));
	}

	@Override
	public double getAltitude() {
		return altitude;
	}

	@Override
	public String filename() {
		return filename;
	}

	@Override
	public String name() {
		return name;
	}

}
