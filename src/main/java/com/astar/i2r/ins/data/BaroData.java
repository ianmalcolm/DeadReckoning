package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaroData extends Data {

	public static final String NAME = "Baro";

	public final double altitude;
	public final double pressure;

	public static final String REGEX = "^LTS:(.+?),AR:(.+?),PV:(.+?)\\n";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public BaroData(Matcher m) {
		super(m.group(1));
		altitude = Double.parseDouble(m.group(2));
		pressure = Double.parseDouble(m.group(3));
	}

}
