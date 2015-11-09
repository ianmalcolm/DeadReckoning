package com.astar.i2r.ins.motion;

import java.util.Date;

public class Speed {

	public final double speed;
	public final Date time;

	public Speed(double spd, long t) {
		speed = spd;
		time = new Date(t);
	}
}
