package com.astar.i2r.ins.map;

import java.util.LinkedList;
import java.util.List;

public class Storey extends LinkedList<Way> {
	public final String name;
	public final String image;
	public final double[] upperleft;
	public final double[] lowerright;
	public final double ele;

	public Storey(String _name, String _image, double[] _ul, double[] _lr,
			double _ele, List<Way> _way) {

		name = _name;
		image = _image;
		upperleft = _ul;
		lowerright = _lr;
		ele = _ele;
		addAll(_way);
	}

	public boolean containsNode(int nid) {
		for (Way w : this) {
			if (w.containsNode(nid)) {
				return true;
			}
		}
		return false;
	}
}
