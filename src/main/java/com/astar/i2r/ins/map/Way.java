package com.astar.i2r.ins.map;

import java.util.LinkedList;
import java.util.List;

public class Way extends LinkedList<Node> {

	public final boolean oneway;
	public final int id;

	public Way(int _id, List<Node> ns, boolean _oneway) {
		id = _id;
		addAll(ns);
		oneway = _oneway;
	}
	
	public boolean containsNode(int nid){
		for (Node n:this){
			if (n.id==nid){
				return true;
			}
		}
		return false;
	}
}
