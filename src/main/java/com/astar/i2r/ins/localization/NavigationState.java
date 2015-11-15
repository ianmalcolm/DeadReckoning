package com.astar.i2r.ins.localization;

import org.apache.log4j.Logger;

public enum NavigationState implements State {

	GPS {
		public boolean process(Context context) {
			if (context.isGPSOK()) {
				context.GPSUpdate();
			} else {
				context.state(NavigationState.SLAM);
			}
			return true;
		}
	},
	SLAM {
		public boolean process(Context context) {
			if (context.isGPSOK()) {
				context.GPSUpdate();
				context.state(NavigationState.GPS);
			} else {
				context.SLAMUpdate();
				context.localize();
			}
			return true;
		}
	};

	private static final Logger log = Logger.getLogger(NavigationState.class
			.getName());

}
