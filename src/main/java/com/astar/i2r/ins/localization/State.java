package com.astar.i2r.ins.localization;

public interface State {
	boolean process(Context context);
	String name();
}
