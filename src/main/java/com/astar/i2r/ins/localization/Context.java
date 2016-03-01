package com.astar.i2r.ins.localization;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.motion.GeoPoint;

public interface Context {
	State state();

	void state(State state);

	void incoming(Data data);

	State needStateSwitch();

	boolean DRUpdate();

	boolean localize();

	boolean GPSUpdate();

	double getRelativeAltitude();

	GeoPoint getGPS();

	String getMapName();

	double[] getMapParameter();

	String getMapFileName();
}
