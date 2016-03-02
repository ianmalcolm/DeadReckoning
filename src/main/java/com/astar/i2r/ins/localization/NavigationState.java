package com.astar.i2r.ins.localization;

import org.apache.log4j.Logger;

public enum NavigationState implements State {

	GPS {
		public boolean process(Context context) {
			State nextState = context.needStateSwitch();
			
			if (nextState == null) {
				this.update(context);
			} else {
				context.state(nextState);
				nextState.update(context);
			}
			
			return true;
		}

		@Override
		public void update(Context context) {
			context.GPSUpdate();
		}
	},
	DR {
		public boolean process(Context context) {
			State nextState = context.needStateSwitch();
			
			if (nextState == null) {
				this.update(context);
			} else {
				context.state(nextState);
				nextState.update(context);
			}
			
			return true;
		}

		@Override
		public void update(Context context) {
			context.DRUpdate();
		}
	};

	private static final Logger log = Logger.getLogger(NavigationState.class
			.getName());

}
