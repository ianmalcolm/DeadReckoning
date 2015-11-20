package com.astar.i2r.ins.map;

import java.awt.geom.Path2D;

public class CarParkTest {

	public CarParkTest() {

	}

	public static void main(String[] args) {
		String filename = "map/Fusionopolis.osm";
		CarPark carpark = new CarPark(filename);

//		Path2D path = new Path2D.Double();
//		path.moveTo(1.29993098195,103.78743551791); path.lineTo(1.29993098195,103.78743551791);
//		path.moveTo(1.29895022751,103.78689940098); path.lineTo(1.29895022751,103.78689940098);
//		path.moveTo(1.29893266766,103.78694382849); path.lineTo(2.29893266766,103.78694382849);
//		path.moveTo(1.29889867479,103.7869306787);  path.lineTo(1.29889867479,103.7869306787);
//		path.moveTo(1.29846906895,103.78794623151); path.lineTo(1.29846906895,103.78794623151);
//		path.moveTo(1.29890997419,103.78813318629); path.lineTo(1.29890997419,103.78813318629);
//		path.moveTo(1.29969247902,103.78800379148); path.lineTo(1.29969247902,103.78800379148);
//		path.moveTo(1.2998934437,103.78752495914);  path.lineTo(1.2998934437,103.78752495914);
//		path.moveTo(1.29993098195,103.78743551791); path.lineTo(1.29993098195,103.78743551791);
//		
//		System.out.println(path.contains(1.2993573,103.7875439));
//		System.out.println(path.contains(1.300335, 103.786535));
		
//		path.moveTo(1.3, 103.7);path.lineTo(1.3, 103.7);
//		path.moveTo(1.3, 103.8);path.lineTo(1.3, 103.8);
//		path.moveTo(1.2, 103.8);path.lineTo(1.2, 103.8);
//		path.moveTo(1.2, 103.7);path.lineTo(1.2, 103.7);
//		path.moveTo(1.3, 103.7);path.lineTo(1.3, 103.7);
//
//		System.out.println(path.getCurrentPoint()==null);
//		path.moveTo(1.3, 103.7);path.lineTo(1.3, 103.7);
//		path.lineTo(1.3, 103.8);
//		path.lineTo(1.2, 103.8);
//		path.lineTo(1.2, 103.7);
//		path.lineTo(1.3, 103.7);
//		System.out.println(path.getCurrentPoint()==null);
//
//		System.out.println(path.contains(1.25,103.75));
//		System.out.println(path.contains(1.300335, 103.886535));

		
		System.out.println(carpark.inBound(1.2993573,103.7875439));
		System.out.println(carpark.inBound(1.300335, 103.786535));
	}
}
