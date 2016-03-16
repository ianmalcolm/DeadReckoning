package com.astar.i2r.ins.map;

import com.astar.i2r.ins.motion.GeoPoint;

public interface Map {
	GeoPoint findClosestSnappedPoint(GeoPoint gp);
}
