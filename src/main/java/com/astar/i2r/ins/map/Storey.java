package com.astar.i2r.ins.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class Storey extends LinkedList<Way> implements Comparable<Storey> {
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

	public double[] getMapParameter() {
		double[] prmt = new double[4];
		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(new File("map/" + image));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int width = bimg.getWidth();
		int height = bimg.getHeight();
		prmt[0] = width / Math.abs(lowerright[1] - upperleft[1]);
		prmt[1] = height / Math.abs(lowerright[0] - upperleft[0]);
		
		prmt[2] = upperleft[0];
		prmt[3] = upperleft[1];
		return prmt;
	}

	@Override
	public int compareTo(Storey arg0) {
		if (ele > arg0.ele) {
			return 1;
		} else if (ele < arg0.ele) {
			return -1;
		} else {
			return 0;
		}
	}

	public double getAltitude() {
		return ele;
	}

	public String filename() {
		return image;
	}
}
