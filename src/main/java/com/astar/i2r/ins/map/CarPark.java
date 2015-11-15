package com.astar.i2r.ins.map;

import java.awt.geom.Path2D;
import java.io.IOException;
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
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class CarPark extends LinkedList<Storey> {
	// the first element store the map of the highest altitude
	private double altitude;
	private String filename;
	private String name;
	private double rotation;
	private double[] scale = new double[] { 0, 0 };
	private double[] reference = new double[] { 0, 0 };
	private Path2D boundary = new Path2D.Double();
	private SimpleDirectedWeightedGraph<Node, DefaultWeightedEdge> graph;

	// private Map<Integer, Node> nodes = new HashMap<Integer, Node>();

	public CarPark(double alt, String fn, String n, double rot, double[] sc,
			double[] ref, Path2D bb) {
		altitude = alt;
		filename = fn;
		name = n;
		rotation = rot;
		for (int i = 0; i < scale.length; i++) {
			scale[i] = sc[i];
			reference[i] = ref[i];
		}
		boundary = bb;
	}

	public CarPark(String filename) {

		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = (Document) builder.build(filename);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}

		boundary = getBBox(doc);
		Map<Integer, Node> nodes = getNodes(doc);
		Map<Integer, Way> ways = getWays(doc, nodes);
		List<Storey> storeies = getStoreies(doc, ways);

		addAll(storeies);

		graph = new SimpleDirectedWeightedGraph<Node, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		for (Way w : ways.values()) {
			for (Node n : w) {
				if (!graph.containsVertex(n)) {
					graph.addVertex(n);
				}
			}
			Node pren = w.getFirst();
			for (Node curn : w) {
				if (pren == curn) {
					continue;
				}
				graph.addEdge(pren, curn);
				if (!w.oneway) {
					graph.addEdge(curn, pren);
				}
				pren = curn;
			}
		}
	}

	public boolean inBound(double lat, double lon) {
		return boundary.contains(lat, lon);
	}

	public double getAltitude() {
		return altitude;
	}

	public String filename() {
		return filename;
	}

	public String name() {
		return name;
	}

	public double[] getMapParameter() {
		double[] prmt = new double[4];
		prmt[0] = scale[0];
		prmt[1] = scale[1];
		prmt[2] = reference[0];
		prmt[3] = reference[1];
		return prmt;
	}

	public static Path2D getBBox(Document doc) {
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
			path.moveTo(lat, lon);
			path.lineTo(lat, lon);
		}
		return path;
	}

	public static Map<Integer, Node> getNodes(Document doc) {
		Map<Integer, Node> nList = new HashMap<Integer, Node>();

		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> exprElement = xFactory.compile(
				"/osm/node[@id and @lat and @lon]", Filters.element());
		List<Element> nodesEle = exprElement.evaluate(doc);
		for (Element nele : nodesEle) {

			Node n = null;
			try {
				int id = nele.getAttribute("id").getIntValue();
				double lat = nele.getAttribute("lat").getDoubleValue();
				double lon = nele.getAttribute("lon").getDoubleValue();
				n = new Node(id, lat, lon, 0);
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
			nList.put(n.id, n);
		}

		return nList;
	}

	public static Map<Integer, Way> getWays(Document doc,
			Map<Integer, Node> nodes) {
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
				List<Node> _ns = new LinkedList<Node>();
				for (Attribute ndAttr : ndAttrs) {
					Node _n = nodes.get(ndAttr.getIntValue());
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

	public static List<Storey> getStoreies(Document doc, Map<Integer, Way> ways) {
		List<Storey> storeies = new LinkedList<Storey>();
		XPathFactory xFactory = XPathFactory.instance();
		XPathExpression<Element> exprElement = xFactory.compile(
				"/osm/relation[@id]", Filters.element());

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
}
