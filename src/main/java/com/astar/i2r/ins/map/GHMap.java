package com.astar.i2r.ins.map;

import java.util.List;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.motion.GeoPoint;
import com.graphhopper.GraphHopper;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

public class GHMap implements Map {

	private final GraphHopper hopper = new GraphHopper().forDesktop();
	private final GraphHopperStorage graph;
	private final LocationIndexMatch index;
	public static final CarFlagEncoder ENCODER = new CarFlagEncoder(5, 5, 1);
	private static final Logger log = Logger.getLogger(GHMap.class.getName());

	private static final GeoCalcTools tools = new GeoCalcTools();

	public GHMap(String osmfile) {

		log.trace("Initializing GraphHpooer map...");
		hopper.setOSMFile(osmfile);
		hopper.setGraphHopperLocation("graphFolder");
		hopper.setEncodingManager(new EncodingManager(ENCODER));
		hopper.setCHEnable(false);
		hopper.importOrLoad();
		graph = hopper.getGraphHopperStorage();

		index = new LocationIndexMatch(graph,
				(LocationIndexTree) hopper.getLocationIndex());

	}

	public WeightedEdge createWeightedEdge(Edge edge) {
		double length = calcEdgeLength(edge);
		SimpleWeightedEdge swe = new SimpleWeightedEdge(edge, length);
		return swe;
	}

	public double calcEdgeLength(Edge edge) {
		final int wayId = edge.getWayId();
		final int adjId = edge.getAdjNodeId();
		GHPoint a = getNode(wayId, adjId, edge.getStartNodeId());
		GHPoint b = getNode(wayId, adjId, edge.getEndNodeId());

		return tools.calcDist(a.lat, a.lon, b.lat, b.lon);
	}

	public GHPoint getPosition(final GHPoint a, final GHPoint b, double r) {
		return tools.calcPointOnEdge(a, b, r);
	}

	public static double calcDist(GHPoint p, GHPoint q) {
		return tools.calcDist(p.lat, p.lon, q.lat, q.lon);
	}

	public GHPoint getNode(final int wayId, final int adjId, final int edgeId) {

		EdgeIteratorState eis = graph.getEdgeIteratorState(wayId, adjId);
		PointList pointList = eis.fetchWayGeometry(3);
		double lat = pointList.getLat(edgeId);
		double lon = pointList.getLon(edgeId);

		return new GHPoint(lat, lon);

	}

	public GHPoint getPosition(PointOnEdge poe) {
		final int wayId = poe.getWayId();
		final int adjId = poe.getAdjNodeId();
		GHPoint a = getNode(wayId, adjId, poe.getStartNodeId());
		GHPoint b = getNode(wayId, adjId, poe.getEndNodeId());

		double dist = poe.getDist();
		return getPosition(a, b, dist);
	}

	@Override
	public GeoPoint findClosestSnappedPoint(GeoPoint gp) {
		List<QueryResult> qResults = index.findNClosest(gp.lat, gp.lon,
				EdgeFilter.ALL_EDGES);
		if (qResults != null) {
			if (qResults.size() > 0) {
				QueryResult first = qResults.get(0);
				GHPoint ghp = first.getSnappedPoint();
				GeoPoint snappedGP = new GeoPoint(ghp.lat, ghp.lon, 0);
				return snappedGP;
			}
		}
		return null;
	}
}
