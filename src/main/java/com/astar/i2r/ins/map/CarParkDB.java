package com.astar.i2r.ins.map;

import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.astar.i2r.ins.motion.GeoPoint;

public class CarParkDB {

	private static Map<Path2D, List<CarPark>> carparks = new HashMap<Path2D, List<CarPark>>();

	static {

		double altCalib = 0;
		double latCalib = 0;
		double lonCalib = 0;
		double scaleCalib = 1;
		double[] alt = { -18.4 + altCalib, -21.4 + altCalib, -24.6 + altCalib,
				-27.8 + altCalib };

		String[] fn = { "AD-FP-B3.png", "AD-FP-B4.png", "AD-FP-B5.png",
				"AD-FP-B6.png" };
		double rot = (-23) / 180 * Math.PI;

		// x and y
		double[] sc = { 3503501.740 * scaleCalib, 3504428.341 * scaleCalib };

		// heading: 2.6441738 from north
		// lat and lon
		double[] rf = { 1.3002204 + latCalib, 103.7866166 + lonCalib };
		// double[] ul = {1.3002204,103.7866166};
		// double[] lr = {1.2982332,103.7888869}; // lower right corner of the
		// image
		// Dimension imageDim = new Dimension(7954,6964); // image size

		Path2D bbox = new Path2D.Double();
		bbox.moveTo(1.3000719, 103.7872963);
		bbox.lineTo(1.3000719, 103.7872963);
		bbox.lineTo(1.2989326, 103.7867392);
		bbox.lineTo(1.2983356, 103.7880767);
		bbox.lineTo(1.2995431, 103.7885511);
		bbox.lineTo(1.3000719, 103.7872963);

		carparks.put(bbox, new LinkedList<CarPark>());
		for (int i = 0; i < 4; i++) {
			carparks.get(bbox).add(
					new CarPark(alt[i], fn[i], fn[i], rot, sc, rf, bbox));
		}
	}

	public static File getMap(String name) {
		File f = new File("map/" + name);
		if (f.exists() && !f.isDirectory()) {
			return f;
		} else {
			return null;
		}
	}

	public static String getMap(double lat, double lon, double ele) {
		List<CarPark> candidates = new LinkedList<CarPark>();
		for (Path2D bbox : carparks.keySet()) {
			if (bbox.contains(lat, lon)) {
				candidates.addAll(carparks.get(bbox));
			}
		}

		if (candidates.isEmpty()) {
			return "";
		}

		Comparator<CarPark> cpt = new Comparator<CarPark>() {
			@Override
			public int compare(CarPark arg0, CarPark arg1) {
				if (arg0.getAltitude() == arg1.getAltitude()) {
					return 1;
				} else {
					return arg0.getAltitude() < arg1.getAltitude() ? -1 : 1;
				}

			}
		};
		Collections.sort(candidates, Collections.reverseOrder(cpt));

		if (ele > candidates.get(0).getAltitude()) {
			return candidates.get(0).filename();
		}
		for (int i = 1; i < candidates.size(); i++) {
			if (ele > candidates.get(i).getAltitude()
					&& ele < candidates.get(i - 1).getAltitude()) {
				return candidates.get(i).filename();
			}
		}

		return "";
	}

	public static double[] getMapParameter(String name) {

		for (List<CarPark> list : carparks.values()) {
			for (CarPark park : list) {
				if (park.name().compareTo(name) == 0) {
					return park.getMapParameter();
				}
			}
		}
		return null;
	}

	public static boolean isInBuilding(double lat, double lon) {
		for (Path2D bbox : carparks.keySet()) {
			if (bbox.contains(lat, lon)) {
				return true;
			}
		}
		return false;
	}

}
