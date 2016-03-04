package com.astar.i2r.ins.map;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CarParkDB {

	private static List<CarPark> carparks = new LinkedList<CarPark>();

	public static void loadMap(String folder) {
		File dir = new File(folder);
		File[] directoryListing = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".osm");
			}
		});
		if (directoryListing != null) {
			for (File child : directoryListing) {
				CarPark cp = null;
				try {
					cp = new CarPark(child.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (cp != null) {
					carparks.add(cp);
				}
			}
		}
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
