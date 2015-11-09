package com.astar.i2r.ins.localization;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.motion.GeoPoint;

public interface Context {
	State state();

	void state(State state);

	void incoming(Data data);

	boolean calibrate();

	boolean isGPSOK();

	boolean step();

	boolean localize();

	boolean GPSUpdate();

	boolean SLAMUpdate();
	
	GeoPoint getGPS();
}
