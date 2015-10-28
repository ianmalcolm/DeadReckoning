package com.astar.i2r.ins.localization;

import java.io.File;

import org.junit.Test;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.Reader;
import com.astar.i2r.ins.localization.AccumulatedMotion;

public class MotionTest {

	@Test
	public void test() {

		File file = new File("/home/ian/Documents/ins/20151015/1444893421.386011.xml");
//		File file = new File("/home/ian/Documents/ins/20151015/1444893164.529593.xml");
		
		Reader reader = new Reader(file);
		AccumulatedMotion track = new AccumulatedMotion();
		for (Data data : reader) {
			track.increment(data);
		}
	}
}

//-m /home/ian/Documents/map/malaysia_singapore_brunei.map -v /home/ian/Documents/ins/20151015/1444893421.386011.MOV -i /home/ian/Documents/ins/20151015/1444893421.386011.xml