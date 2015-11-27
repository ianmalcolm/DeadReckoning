package com.astar.i2r.ins.map;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class CarPark extends LinkedList<Storey> {
	// the first element store the map of the highest altitude

	private String name;
	private Path2D boundary;
	private List<CalibrationBox> calibBoxes;

	public CarPark(String filename) {

		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = (Document) builder.build(filename);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		name = getName(doc);
		boundary = getBBox(doc);
		Map<Integer, GeoNode> nodes = getNodes(doc);
		Map<Integer, Way> ways = getWays(doc, nodes);
		List<Storey> storeies = getStoreies(doc, ways);
		calibBoxes = getCalibBoxes(doc, nodes);
		addAll(storeies);
		Collections.sort(this, Collections.reverseOrder());
	}

	private static List<CalibrationBox> getCalibBoxes(Document doc,
			Map<Integer, GeoNode> nodes) {
		List<CalibrationBox> boxes = new LinkedList<CalibrationBox>();

		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> exprElement = xFactory.compile("/osm/way"
				+ "[@id]" + "[nd]" + "[tag/@k='area' and tag/@v='yes']"
				+ "[tag/@k='headingCalibration']", Filters.element());
		XPathExpression<Attribute> expnd = xFactory.compile("nd/@ref",
				Filters.attribute());
		XPathExpression<Attribute> expheading = xFactory.compile(
				"tag[@k='headingCalibration']/@v", Filters.attribute());

		List<Element> waysEle = exprElement.evaluate(doc);

		for (Element wayEle : waysEle) {
			Path2D path = new Path2D.Double();

			List<Attribute> ndList = expnd.evaluate(wayEle);
			for (Attribute ndAttr : ndList) {
				try {
					GeoNode n = nodes.get(ndAttr.getIntValue());
					if (path.getCurrentPoint() == null) {
						path.moveTo(n.lat, n.lon);
					}
					path.lineTo(n.lat, n.lon);
				} catch (DataConversionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			double heading = 0;
			try {
				heading = expheading.evaluateFirst(wayEle).getDoubleValue();
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int wayId = 0;
			try {
				wayId = wayEle.getAttribute("id").getIntValue();
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			XPathExpression<Attribute> expstorey = xFactory.compile(
					"/osm/relation" + "[member/@type='way' and member/@ref='"
							+ wayId + "']" + "[tag/@k='ele']"
							+ "[tag/@k='image']" + "/tag[@k='name']/@v",
					Filters.attribute());
			String name = expstorey.evaluateFirst(doc).getValue();

			CalibrationBox box = new CalibrationBox(path, heading, name);
			boxes.add(box);
		}

		return boxes;

	}

	private static String getName(Document doc) {

		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Attribute> exprAttribute = xFactory.compile(
				"/osm/relation" + "[member]" + "[tag/@k='name' and tag/@v]"
						+ "[tag/@k='type' and tag/@v='site']"
						+ "[tag/@k='site' and tag/@v='parking']"
						+ "/tag[@k='name']/@v", Filters.attribute());
		Attribute nameAttribute = exprAttribute.evaluateFirst(doc);
		if (nameAttribute == null) {
			exprAttribute = xFactory
					.compile(
							"/osm/way[tag/@k='amenity' and tag/@v='parking']/tag[@k='name']/@v",
							Filters.attribute());
			nameAttribute = exprAttribute.evaluateFirst(doc);
		}
		String name = nameAttribute.getValue();
		return name;

	}

	public boolean inBound(double lat, double lon) {
		return boundary.contains(lat, lon);
	}

	public String name() {
		return name;
	}

	public String getMapName(double alt) {
		double preele = getFirst().ele;
		for (Storey s : this) {
			if (s.ele == preele && alt > s.ele) {
				return s.name;
			} else if (alt < preele && alt > s.ele) {
				return s.name;
			} else {
				preele = s.ele;
			}
		}
		return getLast().name;
	}

	private static Path2D getBBox(Document doc) {
		List<Integer> ndIds = new LinkedList<Integer>();
		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Attribute> exprAttribute = xFactory.compile(
				// "/osm/way[tag@k='amenity' and tag@v='parking']",
				"/osm/way[tag/@k='amenity' and tag/@v='parking']/nd/@ref",
				Filters.attribute());
		List<Attribute> attrs = exprAttribute.evaluate(doc);
		for (Attribute attr : attrs) {
			try {
				ndIds.add(attr.getIntValue());
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assert ndIds.get(0) == ndIds.get(ndIds.size() - 1);

		Path2D path = new Path2D.Double();

		for (int i = 0; i < ndIds.size(); i++) {
			XPathExpression<Element> exprElement = xFactory.compile(
					"/osm/node[@id='" + ndIds.get(i) + "']", Filters.element());
			Iterator<Element> itElem = exprElement.evaluate(doc).iterator();
			Element node = itElem.next();
			double lat = Double.NaN;
			double lon = Double.NaN;
			try {
				lat = node.getAttribute("lat").getDoubleValue();
				lon = node.getAttribute("lon").getDoubleValue();
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
			if (path.getCurrentPoint() == null) {
				path.moveTo(lat, lon);
			}
			path.lineTo(lat, lon);
		}

		return path;
	}

	private static Map<Integer, GeoNode> getNodes(Document doc) {
		Map<Integer, GeoNode> nList = new HashMap<Integer, GeoNode>();

		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> exprElement = xFactory.compile(
				"/osm/node[@id and @lat and @lon]", Filters.element());
		List<Element> nodesEle = exprElement.evaluate(doc);
		for (Element nele : nodesEle) {

			GeoNode n = null;
			try {
				int id = nele.getAttribute("id").getIntValue();
				double lat = nele.getAttribute("lat").getDoubleValue();
				double lon = nele.getAttribute("lon").getDoubleValue();
				n = new GeoNode(id, lat, lon, 0);
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
			nList.put(n.id, n);
		}

		return nList;
	}

	private static Map<Integer, Way> getWays(Document doc,
			Map<Integer, GeoNode> nodes) {
		Map<Integer, Way> ways = new HashMap<Integer, Way>();

		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> expway = xFactory.compile(
				"/osm/way[tag/@k='highway' and tag/@v='service']",
				Filters.element());
		XPathExpression<Element> exponeway = xFactory.compile(
				"tag[@k='oneway' and tag/@v='yes']", Filters.element());
		XPathExpression<Attribute> expnd = xFactory.compile("nd/@ref",
				Filters.attribute());

		List<Element> waysEle = expway.evaluate(doc);
		for (Element wayEle : waysEle) {

			try {
				Element oneway = exponeway.evaluateFirst(wayEle);
				int wayId = wayEle.getAttribute("id").getIntValue();
				List<Attribute> ndAttrs = expnd.evaluate(wayEle);
				List<GeoNode> _ns = new LinkedList<GeoNode>();
				for (Attribute ndAttr : ndAttrs) {
					GeoNode _n = nodes.get(ndAttr.getIntValue());
					_ns.add(_n);
				}
				Way way = new Way(wayId, _ns, oneway != null);
				ways.put(wayId, way);
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ways;

	}

	private static List<Storey> getStoreies(Document doc, Map<Integer, Way> ways) {
		List<Storey> storeies = new LinkedList<Storey>();
		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> exprElement = xFactory.compile("/osm/relation"
				+ "[@id]" + "[tag/@k='ele']" + "[tag/@k='image']",
				Filters.element());

		XPathExpression<Attribute> expname = xFactory.compile(
				"tag[@k='name']/@v", Filters.attribute());
		XPathExpression<Attribute> expele = xFactory.compile(
				"tag[@k='ele']/@v", Filters.attribute());
		XPathExpression<Attribute> expimg = xFactory.compile(
				"tag[@k='image']/@v", Filters.attribute());
		XPathExpression<Attribute> expul = xFactory.compile(
				"tag[@k='upperleft']/@v", Filters.attribute());
		XPathExpression<Attribute> explr = xFactory.compile(
				"tag[@k='lowerright']/@v", Filters.attribute());
		XPathExpression<Attribute> expmem = xFactory.compile("member/@ref",
				Filters.attribute());

		List<Element> rEles = exprElement.evaluate(doc);
		for (Element rel : rEles) {
			try {
				double ele = expele.evaluateFirst(rel).getDoubleValue();
				String name = expname.evaluateFirst(rel).getValue();
				String image = expimg.evaluateFirst(rel).getValue();
				String[] ul = expul.evaluateFirst(rel).getValue().split(",");
				String[] lr = explr.evaluateFirst(rel).getValue().split(",");
				List<Attribute> mAttrs = expmem.evaluate(rel);
				double[] upperleft = new double[] { Double.parseDouble(ul[0]),
						Double.parseDouble(ul[1]) };
				double[] lowerright = new double[] { Double.parseDouble(lr[0]),
						Double.parseDouble(lr[1]) };

				List<Way> ws = new LinkedList<Way>();
				for (Attribute m : mAttrs) {
					Way w = ways.get(m.getIntValue());
					ws.add(w);
				}

				Storey storey = new Storey(name, image, upperleft, lowerright,
						ele, ws);
				storeies.add(storey);
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return storeies;

	}

	public double getCorrectHeading(double lat, double lon) {

		for (CalibrationBox box : calibBoxes) {
			if (box.inBound(lat, lon)) {
				return box.heading;
			}
		}
		return Double.NaN;
	}

	public double[] getMapParameter(String mapName) {

		for (Storey s : this) {
			if (s.name.compareTo(mapName) == 0) {
				return s.getMapParameter();
			}
		}
		return null;
	}

	public String getMapFileName(String mapName) {
		for (Storey s : this) {
			if (s.name.compareTo(mapName) == 0) {
				return s.image;
			}
		}
		return null;
	}
}

class CalibrationBox {
	final String storey;
	final Path2D box;
	final double heading;

	CalibrationBox(Path2D p, double h, String n) {
		box = p;
		heading = h;
		storey = n;
	}

	boolean inBound(double lat, double lon) {
		return box.contains(lat, lon);
	}

}
