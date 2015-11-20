package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

// for debug purpose
public class GroundTruth extends Data{
	public static final String NAME = "GND";

	public final double speedms;
	public final double lat;
	public final double lon;
	public final double heading;
	

	public GroundTruth(Element ele) {
		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());
		lat = Double.parseDouble(ele.getChild("LAT").getText());
		lon = Double.parseDouble(ele.getChild("LON").getText());
		heading = Double.parseDouble(ele.getChild("HD").getText());
		speedms = Double.parseDouble(ele.getChild("SPD").getText());

	}

	public static final String REGEX = "^GND:(.+?),LAT:(.+?),LON:(.+?),HD:(.+?),SPD:(.+?)\\n";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public GroundTruth(Matcher m) {
		super(m.group(1));
		lat = Double.parseDouble(m.group(2));
		lon = Double.parseDouble(m.group(3));
		heading = Double.parseDouble(m.group(4));
		speedms = Double.parseDouble(m.group(5));
	}


}
