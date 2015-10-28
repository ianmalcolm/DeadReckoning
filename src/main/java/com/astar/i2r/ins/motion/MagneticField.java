package com.astar.i2r.ins.motion;

import java.util.Date;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class MagneticField extends Vector3D {

	Date time = new Date();

	public MagneticField(double[] in, long _time) {
		super(in[0],in[1],in[2]);
		time.setTime(_time);
	}

}
