package com.astar.i2r.ins.localization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.mapsforge.core.model.LatLong;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.MagneticData;
import com.astar.i2r.ins.data.MotionData;
import com.astar.i2r.ins.motion.Accelerate;
import com.astar.i2r.ins.motion.Attitude;
import com.astar.i2r.ins.motion.GeoPoint;
import com.astar.i2r.ins.motion.Step;
import com.astar.i2r.ins.motion.Velocity;
import com.graphhopper.GraphHopper;
import com.graphhopper.matching.EdgeMatch;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.shapes.GHPoint3D;

public class GUILocalization {

	List<GeoPoint> track = new ArrayList<GeoPoint>();
	List<Step> steps = new ArrayList<Step>();
	Step step = null;
	GeoPoint lastCalibratedGeoPosition = null;

	LocationIndex index = null;
	Graph baseGraph = null;

	private Attitude attitude = null;
	private Accelerate accelerate = null;
	private Velocity velocity = null;
	private double speedkmh = Double.NaN;

	static final int MINSTEPS = 10;
	static final double ODOINTERVAL = 10;
	static final double CALIBTHRESHOLD = 20;

	GraphHopper hopper = new GraphHopper();
	GraphHopperStorage graph = null;
	LocationIndexMatch locationIndex = null;
	MapMatching mapMatching = null;

	private static final CarFlagEncoder encoder = new CarFlagEncoder(5, 5, 1);

	public GUILocalization(String osmFile, String graphFolder) {
		// create singleton

		hopper.setOSMFile(osmFile);
		hopper.setGraphHopperLocation(graphFolder);
		hopper.setEncodingManager(new EncodingManager(encoder));
		hopper.setCHEnable(false);
		// hopper.clean();
		hopper.importOrLoad();

		graph = hopper.getGraphHopperStorage();
		locationIndex = new LocationIndexMatch(graph,
				(LocationIndexTree) hopper.getLocationIndex());

		mapMatching = new MapMatching(graph, locationIndex, encoder);
		// mapMatching.setMaxSearchMultiplier(50);
		mapMatching.setForceRepair(true);
		mapMatching.setSeparatedSearchDistance(-1);

	}

	public void increment(Data data) {

		if ((attitude == null || accelerate == null || step == null)
				&& data instanceof MotionData) {
			// initialize attitude and accelerate
			attitude = new Attitude(((MotionData) data).cardan, data.time);
			accelerate = new Accelerate(((MotionData) data).accelerate,
					data.time);
			step = new Step(0, 0, 0, data.time);
		}

		if (Double.isNaN(speedkmh) && data instanceof GPSData) {
			speedkmh = ((GPSData) data).speedms / 3.6;
			if (track.size() < 1) {
				// initialize position
				double[] p = ((GPSData) data).gps;
				GeoPoint pos = new GeoPoint(new LatLong(p[0], p[1]),
						data.time);
				track.add(pos);
				lastCalibratedGeoPosition = pos;
			}
			{
				double[] p = ((GPSData) data).gps;
				lastCalibratedGeoPosition = new GeoPoint(new LatLong(p[0],
						p[1]), data.time);
			}
		}

		if (velocity == null && !Double.isNaN(speedkmh) && attitude != null) {
			// initialize velocity
			velocity = new Velocity(attitude.getVelocity(speedkmh),
					attitude.time.getTime());
		}

		if (attitude != null && accelerate != null && velocity != null
				&& track.size() > 0) {
			if (data instanceof MotionData) {
				increment((MotionData) data);
			} else if (data instanceof GPSData) {
				increment((GPSData) data);

			} else if (data instanceof MagneticData) {
				increment((MagneticData) data);
			}
		}

	}

	private void increment(MotionData data) {

		// update attitude
		attitude = new Attitude(data.cardan, data.time);

		// calculate approximated velocity according to the current attitude
		// and GPS/CAN speed
		Vector3D vel = attitude.getVelocity(speedkmh);
		Velocity approxVel = new Velocity(vel, data.time);

		// update position
		// Only for display use. With this user continuously see theirs updated
		// position
		GeoPoint newPosition = track.get(track.size() - 1).increment(
				approxVel);
		track.add(newPosition);

		// accumulate the small steps to reduce the dataset
		step = step.increment(approxVel);

		if (step.getNorm() > Step.MINSTEP) {
			steps.add(step);
			step = new Step(0, 0, 0, data.time);
		}

		// map matching
		if (steps.size() > MINSTEPS) {

			// convert steps and lastCalibratedGeoPosition to List<GPXEntry>
			List<GPXEntry> inputGPXEntries = new ArrayList<GPXEntry>();
			inputGPXEntries.add(lastCalibratedGeoPosition.toGPXEntry());
			for (Step step : steps) {
				GPXEntry lastEntry = inputGPXEntries
						.get(inputGPXEntries.size() - 1);
				inputGPXEntries.add(step.getDestinationFrom(lastEntry));
			}

			double testLat = inputGPXEntries.get(0).lat;

			System.out.println(inputGPXEntries.get(0).lat + ","
					+ inputGPXEntries.get(0).lon + "\t"
					+ inputGPXEntries.get(inputGPXEntries.size() - 1).lat + ","
					+ inputGPXEntries.get(inputGPXEntries.size() - 1).lon);

			MatchResult mr = null;
			boolean calibrated = true;

			try {
				mr = mapMatching.doWork(inputGPXEntries);
			} catch (RuntimeException e) {
				calibrated = false;
			}

			if (calibrated) {

				EdgeMatch em = mr.getEdgeMatches().get(
						mr.getEdgeMatches().size() - 1);
				EdgeFilter filter = new MyEdgeFilter(em.getEdgeState());
				GeoPoint lastGP = track.get(track.size() - 1);
				List<QueryResult> qr = locationIndex.findNClosest(lastGP.lat,
						lastGP.lon, filter);
				GHPoint3D rGHP = qr.get(0).getSnappedPoint();
				lastCalibratedGeoPosition = new GeoPoint(rGHP.lat, rGHP.lon,
						rGHP.ele, data.time);

				// List<GPXEntry> entryList = new GPXFile(mr).getEntries();
				// GPXEntry matchedGe = entryList.get(entryList.size() - 1);
				// lastCalibratedGeoPosition = new GeoPosition(matchedGe.lat,
				// matchedGe.lon, matchedGe.ele, matchedGe.getTime());
				track.add(lastCalibratedGeoPosition);
				steps.clear();
			} else {
				lastCalibratedGeoPosition = track.get(track.size() - 1);
				steps.clear();
			}
		}

	}

	private void increment(GPSData data) {
		speedkmh = data.speedms;
		// Vector2D gpsAcc = new Vector2D(data.accuracy);
		// if (gpsAcc.getNorm() <= 6) {
		// if (data.speedkmh >= 10) {
		// velocity.calibrate(data.speedkmh / 3.6);
		// }
		// }
	}

	private void increment(MagneticData data) {

	}

	public List<GeoPoint> getTrack() {
		return track;
	}

	public GeoPoint getClosestPosition(GeoPoint p) {
		QueryResult qr = index.findClosest(p.lat, p.lon, EdgeFilter.ALL_EDGES);
		GHPoint3D result = qr.getSnappedPoint();
		GeoPoint snappedPosition = new GeoPoint(result.lat, result.lon,
				p.ele, p.time);

		return snappedPosition;
	}

}

class MyEdgeFilter implements EdgeFilter {
	private int base = Integer.MIN_VALUE;
	private int adj = Integer.MIN_VALUE;

	public MyEdgeFilter(EdgeIteratorState _edge) {
		base = _edge.getBaseNode();
		adj = _edge.getAdjNode();
	}

	@Override
	public boolean accept(EdgeIteratorState inEdge) {
		int inbase = inEdge.getBaseNode();
		int inadj = inEdge.getAdjNode();

		if ((base == inbase && adj == inadj)
				|| (base == inadj && adj == inbase)) {
			return true;
		} else {
			return false;
		}
	}
}
