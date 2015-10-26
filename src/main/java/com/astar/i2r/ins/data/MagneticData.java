package com.astar.i2r.ins.data;

import org.jdom2.Element;

public class MagneticData extends Data {

	public static final String NAME = "Magnetic";

	public final double[] magnetic = { 0, 0, 0 };

	public MagneticData(Element ele) {
		super(ele.getAttributeValue(Data.TIMETAG));
		// TODO Auto-generated constructor stub
		magnetic[0] = Double.parseDouble(ele.getChild("MagX").getText());
		magnetic[1] = Double.parseDouble(ele.getChild("MagY").getText());
		magnetic[2] = Double.parseDouble(ele.getChild("MagZ").getText());

	}

}
