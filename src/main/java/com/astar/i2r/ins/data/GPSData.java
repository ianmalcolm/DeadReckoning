package com.astar.i2r.ins.data;

import org.jdom2.Element;

public class GPSData extends Data {

	public static final String NAME = "GPS";

	public final double[] gps = { 0, 0 };
	public final double[] accuracy = { 0, 0 };
	public final double speedkmh;

	public GPSData(Element ele) {
		super(ele.getAttributeValue(Data.TIMETAG));
		// TODO Auto-generated constructor stub
		gps[0] = Double.parseDouble(ele.getChild("Lat").getText());
		gps[1] = Double.parseDouble(ele.getChild("Lon").getText());
		accuracy[0] = Double.parseDouble(ele.getChild("HorAcc").getText());
		accuracy[1] = Double.parseDouble(ele.getChild("VerAcc").getText());
		speedkmh = Double.parseDouble(ele.getChild("Spd").getText());

	}

}
