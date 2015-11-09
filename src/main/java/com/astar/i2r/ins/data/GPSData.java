package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

public class GPSData extends Data {

	public static final String NAME = "GPS";

	public final double[] gps = { 0, 0 };
	public final double[] accuracy = { 0, 0 };
	public final double speedkmh;

	public GPSData(Element ele) {
		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());
		gps[0] = Double.parseDouble(ele.getChild("Lat").getText());
		gps[1] = Double.parseDouble(ele.getChild("Lon").getText());
		accuracy[0] = Double.parseDouble(ele.getChild("HorAcc").getText());
		accuracy[1] = Double.parseDouble(ele.getChild("VerAcc").getText());
		speedkmh = Double.parseDouble(ele.getChild("Spd").getText());

	}

	public static final String REGEX = "^GTS:(.+?),GPA:(.+?),GPO:(.+?),GPH:(.+?),GPV:(.+?),GPS:(.+?)\\n";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public GPSData(Matcher m) {
		super(m.group(1));
		gps[0] = Double.parseDouble(m.group(2));
		gps[1] = Double.parseDouble(m.group(3));
		accuracy[0] = Double.parseDouble(m.group(4));
		accuracy[1] = Double.parseDouble(m.group(5));
		speedkmh = Double.parseDouble(m.group(6));
	}
}
