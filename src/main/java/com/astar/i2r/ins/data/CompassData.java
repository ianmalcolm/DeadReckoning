package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

public class CompassData extends Data {

	public static final String NAME = "Compass";

	public final double magneticDirection;
	public final double rotationHeading;
	public final double[] heading = { 0, 0, 0 };

	public CompassData(Element ele) {
		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());

		magneticDirection = Double.parseDouble(ele
				.getChild("MagneticDirection").getText());
		rotationHeading = Double.parseDouble(ele.getChild("RotationHeading")
				.getText());
		heading[0] = Double.parseDouble(ele.getChild("HeadingX").getText());
		heading[1] = Double.parseDouble(ele.getChild("HeadingY").getText());
		heading[2] = Double.parseDouble(ele.getChild("HeadingZ").getText());

	}

	public static final String REGEX = "^HTS:(\\d+),MD:([\\d.-]+),RH:([\\d.-]+),HX:([\\d.-]+),HY:([\\d.-]+),HZ:([\\d.-]+)";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public CompassData(Matcher m) {
		super(m.group(1));
		magneticDirection = Double.parseDouble(m.group(2));
		rotationHeading = Double.parseDouble(m.group(3));
		heading[0] = Double.parseDouble(m.group(4));
		heading[1] = Double.parseDouble(m.group(5));
		heading[2] = Double.parseDouble(m.group(6));
	}

}
