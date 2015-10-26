package com.astar.i2r.ins.data;

public class Data {

	public static final String NAMETAG = "sensorType";
	public static final String TIMETAG = "relatedTime";

	public final Long time;

	/**
	 * 
	 * @param in
	 *            epoch time in seconds
	 */
	public Data(double in) {
		// TODO Auto-generated constructor stub
		time = (new Double(in * 1000)).longValue();
	}

	/**
	 * 
	 * @param in
	 *            epoch time in seconds
	 */
	public Data(String in) {
		// TODO Auto-generated constructor stub
		this(Double.parseDouble(in));
	}

}
