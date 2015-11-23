package com.astar.i2r.ins.map;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CarParkDB {

	private static List<CarPark> carparks = new LinkedList<CarPark>();

	static {

		String filename = "map/Fusionopolis.osm";
		CarPark cp = new CarPark(filename);

		carparks.add(cp);
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
		for (CarPark park : carparks) {
			if (park.inBound(lat, lon)) {
				return park.getMap(ele);
			}
		}

		return "";
	}

	public static double[] getMapParameter(String name) {

		for (CarPark park : carparks) {
			for (Storey s : park) {
				if (s.image.compareTo(name) == 0) {
					return s.getMapParameter();
				}
			}
		}
		return null;
	}

	public static boolean isInBuilding(double lat, double lon) {
		for (CarPark park : carparks) {
			if (park.inBound(lat, lon)) {
				return true;
			}
		}

		return false;
	}

}
