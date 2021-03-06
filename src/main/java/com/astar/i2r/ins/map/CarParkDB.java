package com.astar.i2r.ins.map;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CarParkDB {

	private static List<CarPark> carparks = new LinkedList<CarPark>();

	static {

		String filename = null;
		CarPark cp = null;
		
		filename = "map/Fusionopolis.osm";
		cp = new CarPark(filename);
		carparks.add(cp);

		filename = "map/Blk88A.osm";
		cp = new CarPark(filename);
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

	public static CarPark getCarPark(double lat, double lon) {
		for (CarPark park : carparks) {
			if (park.inBound(lat, lon)) {
				return park;
			}
		}

		return null;
	}

}
