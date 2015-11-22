package com.astar.i2r.ins.map;

import org.jgrapht.graph.DefaultWeightedEdge;

public class LabelledWeightedEdge extends DefaultWeightedEdge {

	private String label = "";

	public String getLabel() {
		return label;
	}

	public void setLabel(String l) {
		label = l;
	}
}
