package com.astar.i2r.ins.map;

public interface Edge {

	public int getWayId();
	public int getAdjNodeId();

	public int getStartNodeId();
	public int getEndNodeId();
	public boolean isBiDirect();
}
