package com.astar.i2r.ins.motion;

import java.io.File;

import org.junit.Test;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.Reader;
import com.astar.i2r.ins.localization.AccumulatedMotion;

public class MotionTest {

	@Test
	public void test() {

		File file = new File("map/1444893421.386011.xml");
		Reader reader = new Reader(file);
		AccumulatedMotion track = new AccumulatedMotion();
		for (Data data : reader) {
			track.increment(data);
		}
	}
}
