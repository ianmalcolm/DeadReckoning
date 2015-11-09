package com.astar.i2r.ins.map;

public interface CarParkInterface {
	boolean isInBBox(double lat, double lon);

	double getAltitude();

	String filename();

	String name();
}
