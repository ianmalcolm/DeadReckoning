package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

public class MagneticData extends Data {

	public static final String NAME = "Magnetic";

	public final double[] magnetic = { 0, 0, 0 };

	public MagneticData(Element ele) {
		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());
		magnetic[0] = Double.parseDouble(ele.getChild("MagX").getText());
		magnetic[1] = Double.parseDouble(ele.getChild("MagY").getText());
		magnetic[2] = Double.parseDouble(ele.getChild("MagZ").getText());

	}

	public static final String REGEX = "^ATS:(\\d+),MX:([\\d.-]+),MY:([\\d.-]+),MZ:([\\d.-]+)";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public MagneticData(Matcher m) {
		super(m.group(1));
		magnetic[0] = Double.parseDouble(m.group(2));
		magnetic[1] = Double.parseDouble(m.group(3));
		magnetic[2] = Double.parseDouble(m.group(4));
	}

}
