package com.astar.i2r.ins.data;

import java.io.File;

import javax.swing.JFileChooser;

import org.junit.Test;

public class ReaderTest {

	@Test
	public void test() {
		File file = new File("map/1444893421.386011.xml");
		Reader reader = new Reader(file);
		for (int i = 0; i < 10; i++) {
			Data data = reader.next();
			if (MotionData.class.isInstance(data)) {
				System.out.println("Motion");
			} else if (GPSData.class.isInstance(data)) {
				System.out.println("GPS");
			} else if (MagneticData.class.isInstance(data)) {
				System.out.println("Magnetic");
			}
		}
	}

}
