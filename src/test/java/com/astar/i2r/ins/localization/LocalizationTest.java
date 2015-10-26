package com.astar.i2r.ins.localization;

import org.junit.Test;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.RAMDirectory;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.shapes.GHPoint3D;

public class LocalizationTest {
	@Test
	public void test() {

		String osmFile = "/home/ian/Documents/map/malaysia-singapore-brunei-latest.osm.pbf";
		String graphFolder = "graphFolder";
		// create singleton
		GraphHopper hopper = new GraphHopper().forDesktop();
		System.out.println("Create GraphHopper");
//		hopper.setElevation(true);
		hopper.setOSMFile(osmFile);

		// where to store graphhopper files?
		hopper.setGraphHopperLocation(graphFolder);
		hopper.setEncodingManager(new EncodingManager("car"));

		hopper.importOrLoad();
		System.out.println("Load");

		// hopper.
		LocationIndex index = hopper.getLocationIndex();
		System.out.println("LocationIndex");

		double lat = 1.2992625;
		double lon = 103.7866893;

		// index.
		QueryResult qr = index.findClosest(lat, lon, EdgeFilter.ALL_EDGES);
		GHPoint3D result = qr.getSnappedPoint();
		System.out.println(result.toString());

		EdgeIteratorState edge = qr.getClosestEdge();

	}
}
