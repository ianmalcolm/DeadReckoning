package com.astar.i2r.ins.data;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.junit.Ignore;
import org.junit.Test;

public class DataTest {

	@Ignore
	@Test
	public void readerTest() {
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

	@Test
	public void regexTest() {
		String line = "HTS:380351677,MD:-68.731277,RH:68.731277,HX:13.479561,HY:32.610538,HZ:47.294975\n";
		Pattern p = CompassData.PATTERN;
		Matcher m = p.matcher(line);
		boolean found = m.find();
		for (int i = 0; i < 6; i++) {
			System.out.println(m.group(i));
		}
	}
}
