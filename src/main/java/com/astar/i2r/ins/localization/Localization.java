package com.astar.i2r.ins.localization;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.astar.i2r.ins.DataServer;
import com.astar.i2r.ins.INS;
import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.map.GeoMap;
import com.astar.i2r.ins.map.MapWrapper;
import com.graphhopper.GraphHopper;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndexTree;

public class Localization extends Thread {

	private static final Logger log = Logger.getLogger(Localization.class
			.getName());

	private BlockingQueue<Data> dataQ = null;

	private Map<Integer, Context> cList = null;

	

	public Localization(BlockingQueue<Data> _dataQ,
			Map<Integer, Context> _cList) {
		dataQ = _dataQ;
		cList = _cList;
		Context car = new Vehicle();
		cList.put(INS.car0, car);

	}

	@Override
	public void run() {
		while (true) {

			Data data = null;
			try {
				data = dataQ.take();
				log.trace("Receive a " + data.getClass().getSimpleName());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			assert data != null;

			Context car = cList.get(INS.car0);
			car.incoming(data);
			car.state().process(car);
		}
	}
}
