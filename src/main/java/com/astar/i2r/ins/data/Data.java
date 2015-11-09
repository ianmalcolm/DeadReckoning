package com.astar.i2r.ins.data;

public abstract class Data {

	public static final String NAMETAG = "sensorType";
	public static final String TIMETAG = "relatedTime";
	// public static final Pattern timePattern = Pattern
	// .compile("^\\p{Upper}[3]:(.+?),");

	public final Long time;

	/**
	 * 
	 * @param in
	 *            epoch time in seconds
	 */
	public Data(long in) {
		// TODO Auto-generated constructor stub
		time = in;
	}

	/**
	 * 
	 * @param in
	 *            epoch time in seconds
	 */

	public Data(String in) {
		this(Long.parseLong(in));
	}

}
