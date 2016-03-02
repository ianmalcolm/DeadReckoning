package com.astar.i2r.ins.localization;

public interface State {
	void update(Context context);
	boolean process(Context context);
	String name();
}
