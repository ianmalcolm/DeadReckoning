package com.astar.i2r.ins.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Element;

public class MotionData extends Data {

	public static final String NAME = "Motion";

	public final double[] gravity = { 0, 0, 0 };
	public final double[] accelerate = { 0, 0, 0 };
	public final double[] inversedCardan = { 0, 0, 0 };
	public final double[] cardan = { 0, 0, 0 };
	public final double[] rotationRate = { 0, 0, 0 };
//	public final double[] magnetic = { 0, 0, 0 };
//	public final double magneticAccuracy;
//	public final double[] heading = { 0, 0, 0 };

	public MotionData(Element ele) {

		super((new Double(Double.parseDouble(ele
				.getAttributeValue(Data.TIMETAG)) * 1000)).longValue());
		gravity[0] = Double.parseDouble(ele.getChild("GraX").getText());
		gravity[1] = Double.parseDouble(ele.getChild("GraY").getText());
		gravity[2] = Double.parseDouble(ele.getChild("GraZ").getText());
		accelerate[0] = Double.parseDouble(ele.getChild("AccX").getText());
		accelerate[1] = Double.parseDouble(ele.getChild("AccY").getText());
		accelerate[2] = Double.parseDouble(ele.getChild("AccZ").getText());
		inversedCardan[0] = Double.parseDouble(ele.getChild("Roll").getText());
		inversedCardan[1] = Double.parseDouble(ele.getChild("Pitch").getText());
		inversedCardan[2] = Double.parseDouble(ele.getChild("Yaw").getText());
		cardan[0] = Double.parseDouble(ele.getChild("RelRoll").getText());
		cardan[1] = Double.parseDouble(ele.getChild("RelPitch").getText());
		cardan[2] = Double.parseDouble(ele.getChild("RelYaw").getText());
		rotationRate[0] = Double.parseDouble(ele.getChild("RotRatX").getText());
		rotationRate[1] = Double.parseDouble(ele.getChild("RotRatY").getText());
		rotationRate[2] = Double.parseDouble(ele.getChild("RotRatZ").getText());
//		magnetic[0] = Double.parseDouble(ele.getChild("MagX").getText());
//		magnetic[1] = Double.parseDouble(ele.getChild("MagY").getText());
//		magnetic[2] = Double.parseDouble(ele.getChild("MagZ").getText());
//		magneticAccuracy = Double.parseDouble(ele.getChild("MagAcc").getText());

	}

//	public static final String REGEX = "^MTS:(.+?),GX:(.+?),GY:(.+?),GZ:(.+?),AX:(.+?),AY:(.+?),AZ:(.+?),TR:(.+?),TP:(.+?),TY:(.+?),RR:(.+?),RP:(.+?),RY:(.+?),RX:(.+?),RY:(.+?),RZ:(.+?),MX:(.+?),MY:(.+?),MZ:(.+?),MA:(.+?),HX:(.+?),HY:(.+?),HZ:(.+?)\\n";
	public static final String REGEX = "^MTS:(.+?),GX:(.+?),GY:(.+?),GZ:(.+?),AX:(.+?),AY:(.+?),AZ:(.+?),TR:(.+?),TP:(.+?),TY:(.+?),RR:(.+?),RP:(.+?),RY:(.+?),RX:(.+?),RY:(.+?),RZ:(.+?),";
	public static final Pattern PATTERN = Pattern.compile(REGEX);

	public MotionData(Matcher m) {
		super(m.group(1));
		gravity[0] = Double.parseDouble(m.group(2));
		gravity[1] = Double.parseDouble(m.group(3));
		gravity[2] = Double.parseDouble(m.group(4));
		accelerate[0] = Double.parseDouble(m.group(5));
		accelerate[1] = Double.parseDouble(m.group(6));
		accelerate[2] = Double.parseDouble(m.group(7));
		inversedCardan[0] = Double.parseDouble(m.group(8));
		inversedCardan[1] = Double.parseDouble(m.group(9));
		inversedCardan[2] = Double.parseDouble(m.group(10));
		cardan[0] = Double.parseDouble(m.group(11));
		cardan[1] = Double.parseDouble(m.group(12));
		cardan[2] = Double.parseDouble(m.group(13));
		rotationRate[0] = Double.parseDouble(m.group(14));
		rotationRate[1] = Double.parseDouble(m.group(15));
		rotationRate[2] = Double.parseDouble(m.group(16));
//		magnetic[0] = Double.parseDouble(m.group(17));
//		magnetic[1] = Double.parseDouble(m.group(18));
//		magnetic[2] = Double.parseDouble(m.group(19));
//		magneticAccuracy = Double.parseDouble(m.group(20));
//		heading[0] = Double.parseDouble(m.group(21));
//		heading[1] = Double.parseDouble(m.group(22));
//		heading[2] = Double.parseDouble(m.group(23));
	}
}
