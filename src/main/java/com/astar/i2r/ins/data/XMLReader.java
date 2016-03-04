package com.astar.i2r.ins.data;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XMLReader implements Iterable<Data>, Iterator<Data> {

	public static final HashMap<String, Class<?>> SENSORDATATYPE;
	static {
		SENSORDATATYPE = new HashMap<String, Class<?>>();
		SENSORDATATYPE.put(MotionData.NAME, MotionData.class);
		SENSORDATATYPE.put(GPSData.NAME, GPSData.class);
		SENSORDATATYPE.put(MagneticData.NAME, MagneticData.class);
	}

	private Document doc = null;
	private Iterator<Element> it = null;

	public XMLReader(File file, String name) {
		SAXBuilder builder = new SAXBuilder();
		try {

			doc = (Document) builder.build(file);
			doc.getRootElement();

			// use the default implementation
			XPathFactory xFactory = XPathFactory.instance();
			// System.out.println(xFactory.getClass());

			// select all data for motion sensor
			XPathExpression<Element> expr = xFactory.compile(
					"/SensorData/data[@" + Data.NAMETAG + "='" + name + "']",
					Filters.element());

			it = expr.evaluate(doc).iterator();

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
	}

	public XMLReader(File file) {
		SAXBuilder builder = new SAXBuilder();
		try {

			doc = (Document) builder.build(file);
			doc.getRootElement();

			// use the default implementation
			XPathFactory xFactory = XPathFactory.instance();
			// System.out.println(xFactory.getClass());

			// select all data for motion sensor
			XPathExpression<Element> expr = xFactory.compile(
					"/SensorData/data", Filters.element());

			it = expr.evaluate(doc).iterator();

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		}
	}

	@Override
	public Data next() {
		// TODO Auto-generated method stub
		Data data = null;
		if (it.hasNext()) {
			Element ele = it.next();
			Class<?> clazz = SENSORDATATYPE.get(ele
					.getAttributeValue(Data.NAMETAG));
			try {
				Constructor<?> constructor = clazz
						.getConstructor(Element.class);
				data = (Data) constructor.newInstance(ele);

			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return it.hasNext();
	}

	@Override
	public Iterator<Data> iterator() {
		// TODO Auto-generated method stub
		return this;
	}

}
