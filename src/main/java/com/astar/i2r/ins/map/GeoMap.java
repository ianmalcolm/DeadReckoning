package com.astar.i2r.ins.map;

import java.util.List;

import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Step;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.util.PointList;

public interface GeoMap {
	GeoPoint localize(List<GeoPoint> steps);

	GeoPoint step(GeoPoint p, GeoPoint q);
}
