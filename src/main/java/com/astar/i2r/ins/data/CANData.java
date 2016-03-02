package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

public class CANData extends Data {

	public static final String NAME = "CAN";

	public final int vehSpdkmh;

	public CANData(Element ele) {
		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());

		// TODO Auto-generated constructor stub
		vehSpdkmh = Integer.parseInt(ele.getChild("VehSpd").getText(), 16);

	}

	public static final String REGEX = "^CAN:(\\d+),.+41 0D (..)";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public CANData(Matcher m) {
		super(m.group(1));
		vehSpdkmh = Integer.parseInt(m.group(2),16);
	}

}
