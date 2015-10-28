package com.astar.i2r.ins.localization;

import java.io.File;

import org.junit.Test;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.Reader;

public class LocalizationTest {
	@Test
	public void test() {

		String osmFile = "/home/ian/Documents/map/malaysia-singapore-brunei-latest.osm.pbf";
		String graphFolder = "graphFolder";

		File file = new File(
				"/home/ian/Documents/ins/20151015/1444893421.386011.xml");
		
		Reader reader = new Reader(file);
		Localization track = new Localization(osmFile, graphFolder);
		for (Data data : reader) {
			track.increment(data);
		}

	}

}
