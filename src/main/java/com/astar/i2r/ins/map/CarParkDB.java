package com.astar.i2r.ins.map;

import java.awt.Polygon;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.astar.i2r.ins.motion.GeoPoint;

public class CarParkDB {

	private static List<CarParkInterface> carparks = new LinkedList<CarParkInterface>();

	static {

		// private double altitude;
		// private String filename;
		// private String name;
		// private double rotation;
		// private double scale;
		// private GeoPoint reference = new GeoPoint(1.2992444, 103.7875234, 0,
		// 0);
		// private Polygon bbox;

		double[] alt = { -12, -16, -20, -24 };
		String[] fn = { "AD-FP-B3.png", "AD-FP-B4.png", "AD-FP-B5.png",
				"AD-FP-B6.png" };
		double rot = (-23) / 180 * Math.PI;
		double sc = 0;
		GeoPoint rf = new GeoPoint(1.2992444, 103.7875234, 0, 0);
		Polygon bbox = new Polygon();
		bbox.addPoint(CarPark.toInt(1.3000719), CarPark.toInt(103.7872963));
		bbox.addPoint(CarPark.toInt(1.2989326), CarPark.toInt(103.7867392));
		bbox.addPoint(CarPark.toInt(1.2983356), CarPark.toInt(103.7880767));
		bbox.addPoint(CarPark.toInt(1.2995431), CarPark.toInt(103.7885511));

		for (int i = 0; i < 4; i++) {
			carparks.add(new CarPark(alt[i], fn[i], fn[i], rot, sc, rf, bbox));
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

	public static String getMap(String latStr, String lonStr, String eleStr) {
		List<CarParkInterface> candidates = new LinkedList<CarParkInterface>();
		for (CarParkInterface park : carparks) {
			if (park.isInBBox(Double.parseDouble(latStr),
					Double.parseDouble(lonStr))) {
				candidates.add(park);
			}
		}

		if (candidates.isEmpty()) {
			return "";
		}

		Comparator<CarParkInterface> cpt = new Comparator<CarParkInterface>() {
			@Override
			public int compare(CarParkInterface arg0, CarParkInterface arg1) {
				if (arg0.getAltitude() == arg1.getAltitude()) {
					return 1;
				} else {
					return arg0.getAltitude() < arg1.getAltitude() ? -1 : 1;
				}

			}
		};
		Collections.sort(candidates, Collections.reverseOrder(cpt));

		double ele = Double.parseDouble(eleStr);

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

}
