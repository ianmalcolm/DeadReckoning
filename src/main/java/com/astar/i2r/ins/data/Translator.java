package com.astar.i2r.ins.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom2.Element;

public class Translator {

	private static final Logger log = Logger.getLogger(Translator.class);

	public static final Map<Pattern, Class<? extends Data>> SENSORDATATYPE = new HashMap<Pattern, Class<? extends Data>>();;
	static {
		SENSORDATATYPE.put(MotionData.PATTERN, MotionData.class);
		SENSORDATATYPE.put(GPSData.PATTERN, GPSData.class);
		SENSORDATATYPE.put(MagneticData.PATTERN, MagneticData.class);
		SENSORDATATYPE.put(BaroData.PATTERN, BaroData.class);
		SENSORDATATYPE.put(CompassData.PATTERN, CompassData.class);
		SENSORDATATYPE.put(CANData.PATTERN, CANData.class);
		
		// for debug purpose
		SENSORDATATYPE.put(GroundTruth.PATTERN, GroundTruth.class);
	}

	public static Data translate(String line) {

		try {

			for (Pattern p : SENSORDATATYPE.keySet()) {
				Matcher m = p.matcher(line);
				if (!m.find()) {
					continue;
				}
				Class<? extends Data> clazz = SENSORDATATYPE.get(p);
				Constructor<? extends Data> constructor;
				constructor = clazz.getConstructor(Matcher.class);
				Data data = (Data) constructor.newInstance(m);

				return data;
			}

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
