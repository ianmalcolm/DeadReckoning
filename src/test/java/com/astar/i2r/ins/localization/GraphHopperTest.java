package com.astar.i2r.ins.localization;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

public class GraphHopperTest {

	// enable turn restrictions in encoder:
	private static final EncodingManager encoder = new EncodingManager();
	private static final TestGraphHopper hopper = (TestGraphHopper) new TestGraphHopper()
			.forDesktop();

	@BeforeClass
	public static void doImport() {
		hopper.setOSMFile("/home/ian/Documents/map/malaysia-singapore-brunei-latest.osm.pbf");
		hopper.setGraphHopperLocation("graphFolder");
		hopper.setEncodingManager(encoder);
		hopper.setCHEnable(false);
		// hopper.clean();
		hopper.importOrLoad();
	}

	@AfterClass
	public static void doClose() {
		hopper.close();
	}

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(GraphHopperTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println(result.wasSuccessful());
	}

	@Test
	public void findClosestTest() {

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

	@Test
	public void mapMatchingTest() {
		GraphHopperStorage graph = hopper.getGraphHopperStorage();
		LocationIndexMatch locationIndex = new LocationIndexMatch(graph,
				(LocationIndexTree) hopper.getLocationIndex());

		MapMatching mapMatching = new MapMatching(graph, locationIndex,
				encoder.getEncoder(EncodingManager.CAR));
		mapMatching.setSeparatedSearchDistance(30);

		// printOverview(graph, hopper.getLocationIndex(), 51.358735, 12.360574,
		// 500);
		List<GPXEntry> inputGPXEntries = createRandomGPXEntries(new GHPoint(
				51.358735, 12.360574), new GHPoint(51.358594, 12.360032));
		MatchResult mr = mapMatching.doWork(inputGPXEntries);

	}

	private List<GPXEntry> createRandomGPXEntries(GHPoint start, GHPoint end) {
		GHResponse ghr = hopper.route(new GHRequest(start, end)
				.setWeighting("fastest"));
		return hopper.getEdges(0);
	}

	// use a workaround to get access to
	static class TestGraphHopper extends GraphHopper {

		private List<Path> paths;

		List<GPXEntry> getEdges(int index) {
			Path path = paths.get(index);
			Translation tr = getTranslationMap().get("en");
			InstructionList instr = path.calcInstructions(tr);
			// GPXFile.write(path, "calculated-route.gpx", tr);
			return instr.createGPXList();
		}

		@Override
		protected List<Path> getPaths(GHRequest request, GHResponse rsp) {
			paths = super.getPaths(request, rsp);
			return paths;
		}
	}

}
