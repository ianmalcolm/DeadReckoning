package com.astar.i2r.ins.localization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.mapsforge.core.model.LatLong;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.MagneticData;
import com.astar.i2r.ins.data.MotionData;
import com.astar.i2r.ins.motion.Accelerate;
import com.astar.i2r.ins.motion.Attitude;
import com.astar.i2r.ins.motion.GeoPosition;
import com.astar.i2r.ins.motion.Velocity;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;

public class Localization {

	List<GeoPosition> track = new ArrayList<GeoPosition>();
	// List<Position> estimatedTrack = new ArrayList<Position>();
	// List<Data> imuData = new ArrayList<Data>();
	LocationIndex index = null;
	Graph baseGraph = null;

	private Attitude attitude = null;
	private Rotation attitudeCalibration = new Rotation(RotationOrder.ZXZ, 0,
			0, 0);
	private Accelerate accelerate = null;
	private Velocity velocity = null;
	private double speedkmh = Double.NaN;
	private double odometry = 0;

	static double ODOINTERVAL = 10;
	static double CALIBTHRESHOLD = 20;

	public Localization(String osmFile, String graphFolder) {

		// String osmFile =
		// "/home/ian/Documents/map/malaysia-singapore-brunei-latest.osm.pbf";
		// String graphFolder = "graphFolder";

		// create singleton
		GraphHopper hopper = new GraphHopper().forDesktop();
		// hopper.setElevation(true);
		hopper.setOSMFile(osmFile);

		// where to store graphhopper files?
		hopper.setGraphHopperLocation(graphFolder);
		hopper.setEncodingManager(new EncodingManager("car"));
		hopper.importOrLoad();

		baseGraph = hopper.getGraphHopperStorage().getBaseGraph();
		index = hopper.getLocationIndex();

	}

	public void increment(Data data) {

		if ((attitude == null || accelerate == null)
				&& data instanceof MotionData) {
			// initialize attitude and accelerate
			attitude = new Attitude(((MotionData) data).cardan, data.time);
			accelerate = new Accelerate(((MotionData) data).accelerate,
					data.time);
		}

		if (Double.isNaN(speedkmh) && data instanceof GPSData) {
			speedkmh = ((GPSData) data).speedkmh / 3.6;
			if (track.size() < 1) {
				// initialize position
				double[] p = ((GPSData) data).gps;
				GeoPosition pos = new GeoPosition(new LatLong(p[0], p[1]), data.time);
				track.add(pos);
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
		// need a calibration
		// attitude = attitude.calibrate(attitudeCalibration);

		Vector3D vel = attitude.getVelocity(speedkmh);
		if (odometry > ODOINTERVAL) {
			vel = attitudeCalibration.applyTo(vel);
			// System.out.println(attitudeCalibration.applyTo(
			// new Vector3D(0, 1, 0)).toString());
		}
		Velocity approxVel = new Velocity(vel, data.time);

		// update position
		GeoPosition newPosition = track.get(track.size() - 1).increment(approxVel);
		// Position newPosition = get(size() - 1).increment(velocity);

		double diff = data.time / 1000.0
				- track.get(track.size() - 1).time.getTime() / 1000.0;

		if (odometry > ODOINTERVAL) {
			newPosition = getClosestPosition(newPosition);
			odometry = 0;
		}

		odometry += speedkmh * diff;
		Rotation newCalib = calCalibration(newPosition, attitude);
		if (newCalib != null) {
			attitudeCalibration = newCalib;
		}

		track.add(newPosition);

	}

	private void increment(GPSData data) {
		speedkmh = data.speedkmh;
		// Vector2D gpsAcc = new Vector2D(data.accuracy);
		// if (gpsAcc.getNorm() <= 6) {
		// if (data.speedkmh >= 10) {
		// velocity.calibrate(data.speedkmh / 3.6);
		// }
		// }
	}

	private void increment(MagneticData data) {

	}

	public List<GeoPosition> getTrack() {
		return track;
	}

	public GeoPosition getClosestPosition(GeoPosition p) {
		QueryResult qr = index.findClosest(p.lat, p.lon, EdgeFilter.ALL_EDGES);
		GHPoint3D result = qr.getSnappedPoint();
		GeoPosition snappedPosition = new GeoPosition(result.lat, result.lon, p.ele,
				p.time);

		return snappedPosition;
	}

	public Rotation calCalibration(GeoPosition p, Attitude curAtt) {
		QueryResult qr = index.findClosest(p.lat, p.lon, EdgeFilter.ALL_EDGES);
		GHPoint3D result = qr.getSnappedPoint();
		GeoPosition snappedPosition = new GeoPosition(result.lat, result.lon, p.ele,
				p.time);

		EdgeIteratorState edge = qr.getClosestEdge();
		// baseGraph.;

		GeoPosition basePosition = null;
		GeoPosition adjPosition = null;
		{
			int adjNodeId = edge.getAdjNode();
			int baseNodeId = edge.getBaseNode();
			double baselat = baseGraph.getNodeAccess().getLat(baseNodeId);
			double baselon = baseGraph.getNodeAccess().getLon(baseNodeId);
			// double baseele = baseGraph.getNodeAccess().getEle(baseNodeId);
			double adjlat = baseGraph.getNodeAccess().getLat(adjNodeId);
			double adjlon = baseGraph.getNodeAccess().getLon(adjNodeId);
			// double adjele = baseGraph.getNodeAccess().getEle(adjNodeId);
			basePosition = new GeoPosition(baselat, baselon, 0, p.time);
			adjPosition = new GeoPosition(adjlat, adjlon, 0, p.time);
		}
		double edgeLen = edge.getDistance();
		double baseSnapDist = GeoPosition.distance(basePosition, snappedPosition)
				.getNorm();
		double adjSnapDist = GeoPosition.distance(adjPosition, snappedPosition)
				.getNorm();

		if (edgeLen < CALIBTHRESHOLD || baseSnapDist < CALIBTHRESHOLD
				|| adjSnapDist < CALIBTHRESHOLD) {
			return null;
		}

		Vector3D roadV = GeoPosition.distance(basePosition, adjPosition);
		Vector3D currV = curAtt.getVelocity(1);
		double angle = Vector3D.angle(currV, roadV);
		if (angle > Math.PI / 2) {
			roadV = roadV.scalarMultiply(-1);
		}
		Vector3D attiV = curAtt.getVelocity(1);
		Rotation newCalib = new Rotation(RotationOrder.ZXZ, roadV.getAlpha()
				- attiV.getAlpha(), 0, 0);

		return newCalib;
	}

}
