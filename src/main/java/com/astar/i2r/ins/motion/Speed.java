package com.astar.i2r.ins.motion;

import java.util.Date;

public class Speed {

	public final double speedms;
	public final Date time;

	public Speed(double spdms, long t) {
		speedms = spdms;
		time = new Date(t);
	}
}
