package com.astar.i2r.ins.localization;

import org.apache.log4j.Logger;

public enum NavigationState implements State {

	GPS {
		public boolean process(Context context) {
			State nextState = context.needStateSwitch();
			if (nextState == null) {
				context.GPSUpdate();
			} else {
				context.state(nextState);
			}
			return true;
		}
	},
	SLAM {
		public boolean process(Context context) {
			State nextState = context.needStateSwitch();
			if (nextState == null) {
				context.SLAMUpdate();
				context.localize();
			} else {
				context.state(nextState);
			}
			return true;
		}
	};

	private static final Logger log = Logger.getLogger(NavigationState.class
			.getName());

}
