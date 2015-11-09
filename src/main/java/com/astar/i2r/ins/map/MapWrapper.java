package com.astar.i2r.ins.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Step;
import com.graphhopper.GraphHopper;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.storage.index.QueryResult.Position;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.shapes.GHPoint3D;

public class MapWrapper implements GeoMap {

	GraphHopper hopper = new GraphHopper();
	GraphHopperStorage graph = null;
	LocationIndexMatch index = null;
	MapMatching mapMatching = null;

	private static final CarFlagEncoder encoder = new CarFlagEncoder(5, 5, 1);

	public MapWrapper() {
		// create singleton

//		hopper.setOSMFile(osmFile);
		hopper.setGraphHopperLocation("graphFolder");
		hopper.setEncodingManager(new EncodingManager(encoder));
		hopper.setCHEnable(false);
		// hopper.clean();
		hopper.importOrLoad();

		graph = hopper.getGraphHopperStorage();
		index = new LocationIndexMatch(graph,
				(LocationIndexTree) hopper.getLocationIndex());
		mapMatching = new MapMatching(graph, index, encoder);
		mapMatching.setForceRepair(true);
		mapMatching.setSeparatedSearchDistance(-1);
	}

	@Override
	public GeoPoint localize(List<GeoPoint> steps) {
		List<GPXEntry> entries = new ArrayList<GPXEntry>();
		for (GeoPoint step : steps) {
			entries.add(step.toGPXEntry());
		}
		MatchResult mr = null;

		try {
			mr = mapMatching.doWork(entries);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	@Override
	public GeoPoint step(GeoPoint p, GeoPoint q) {
		// if p and q are on the same road, then estimated position
		Vector3D v = GeoPoint.distance(p, q);
		GHPoint3D snappedQ = index.findClosest(q.lat, q.lon,
				EdgeFilter.ALL_EDGES).getSnappedPoint();
		Vector3D snappedV = GeoPoint.distance(p, new GeoPoint(
				snappedQ.lat, snappedQ.lon, q.ele, q.time.getTime()));
		double dist = v.getNorm();
		double snappedDist = snappedV.getNorm();
		v = snappedV.scalarMultiply(dist / snappedDist);
		GeoPoint scaledQ = p.add(new Step(v, q.time.getTime()));

		QueryResult fromQR = index.findClosest(p.lat, p.lon,
				EdgeFilter.ALL_EDGES);

		QueryResult toQR = index.findClosest(scaledQ.lat, scaledQ.lon,
				EdgeFilter.ALL_EDGES);

		if (fromQR.getClosestEdge().getEdge() == toQR.getClosestEdge().getEdge()) {
			GHPoint3D calibQ = toQR.getSnappedPoint();
			return new GeoPoint(calibQ.lat, calibQ.lon, q.ele,
					q.time.getTime());
		} else {
			return null;
		}
	}
}
