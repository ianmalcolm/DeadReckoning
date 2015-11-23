package com.astar.i2r.ins.map;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.astar.i2r.ins.motion.GeoPoint;

public class Node {
	public final int id;
	public final double lat;
	public final double lon;
	public final double ele;

	public Node(int _id, double _lat, double _lon, double _ele) {
		id = _id;
		lat = _lat;
		lon = _lon;
		ele = _ele;
	}

	public static Vector3D dist(Node p, Node q) {
		return GeoPoint.distance(new GeoPoint(p.lat, p.lon, p.ele),
				new GeoPoint(q.lat, q.lon, q.ele));
	}
	
	public String toString(){
		return Integer.toString(id);
	}
}
