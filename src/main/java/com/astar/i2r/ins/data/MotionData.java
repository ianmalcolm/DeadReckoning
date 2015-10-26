package com.astar.i2r.ins.data;

import org.jdom2.Element;

public class MotionData extends Data {

	public static final String NAME = "Motion";

	public final double[] gravity = { 0, 0, 0 };
	public final double[] accelerate = { 0, 0, 0 };
	public final double[] inversedCardan = { 0, 0, 0 };
	public final double[] cardan = { 0, 0, 0 };
	public final double[] rotationRate = { 0, 0, 0 };
	public final double[] magnetic = { 0, 0, 0 };
	public final double magneticAccuracy;

	public MotionData(Element ele) {
		super(ele.getAttributeValue(Data.TIMETAG));
		// TODO Auto-generated constructor stub
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
		magnetic[0] = Double.parseDouble(ele.getChild("MagX").getText());
		magnetic[1] = Double.parseDouble(ele.getChild("MagY").getText());
		magnetic[2] = Double.parseDouble(ele.getChild("MagZ").getText());
		magneticAccuracy = Double.parseDouble(ele.getChild("MagAcc").getText());
	}

}
