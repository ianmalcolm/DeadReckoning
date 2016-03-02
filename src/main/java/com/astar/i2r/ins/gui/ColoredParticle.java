package com.astar.i2r.ins.gui;

import java.awt.Color;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.util.shapes.GHPoint;
import com.astar.i2r.ins.gui.Colored;
import com.astar.i2r.ins.gui.ColoredWeightedWaypoint;
import com.astar.i2r.ins.gui.Weighted;
import com.astar.i2r.ins.motion.GeoPoint;

public class ColoredParticle implements ColoredWeightedWaypoint {

	public final GeoPosition pos;
	public final double weight;
	public final Color color;

	public static final double DEFAULTWEIGHT = 1.0;
	public static final Color DEFAULTCOLOR = Color.RED;

	public ColoredParticle(GHPoint p, double w, Color c) {
		pos = new GeoPosition(p.lat, p.lon);
		weight = w;
		color = c;
	}

	public ColoredParticle(GHPoint p) {
		this(p, DEFAULTWEIGHT, DEFAULTCOLOR);
	}

	public ColoredParticle(GeoPoint p) {
		this(p.lat, p.lon, DEFAULTWEIGHT, DEFAULTCOLOR);
	}

	public ColoredParticle(double lat, double lon, double w, Color c) {
		pos = new GeoPosition(lat, lon);
		weight = w;
		color = c;
	}

	@Override
	public GeoPosition getPosition() {
		return pos;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public Weighted setWeight(double w) {
		return new ColoredParticle(pos.getLatitude(), pos.getLongitude(), w,
				color);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Colored setColor(Color c) {
		return new ColoredParticle(pos.getLatitude(), pos.getLongitude(),
				weight, c);
	}

}
