package com.astar.i2r.ins.data;

//import java.awt.Polygon;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import com.astar.i2r.ins.map.CarPark;
import com.astar.i2r.ins.map.GeoNode;

public class XPathTest {

	@Test
	public void bboxtest() {

		CarPark park = new CarPark("map/Fusionopolis.osm");

	}

	@Test
	public void gistest() {

		Path2D path = new Path2D.Double();
		path.moveTo(0, 0);
		// path.lineTo(0, 0);
		// path.moveTo(0, 1);
		path.lineTo(0, 1);
		// path.moveTo(1, 1);
		path.lineTo(1, 1);
		// path.moveTo(1, 0);
		path.lineTo(1, 0);

		path.lineTo(0, 0);
		// path.closePath();
		System.out.println(path.getBounds().toString());
		// System.out.println(path.getWindingRule());
		// System.out.println(path.contains(0.5, 0.5));
		// System.out.println(path.contains(-0.5, -0.5));

	}

}
