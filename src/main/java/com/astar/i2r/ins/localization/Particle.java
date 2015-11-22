package com.astar.i2r.ins.localization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.astar.i2r.ins.map.CarParkGraph;
import com.astar.i2r.ins.map.GeoNode;

public class Particle {

	double weight = 1;
	GeoNode src = null;
	GeoNode dst = null;
	double dist = 0;
	Vector3D velocity;

	public Particle() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Measure the probability of the particle
	 * 
	 * @return probability
	 */
	double probability() {

		return dist;

	}

	void move(CarParkGraph map) {
		double curWeight = map.getEdgeWeight(map.getEdge(src, dst));
	}

	void sense(Vector3D attitude, Vector3D velocityChange) {

	}

	/**
	 * Resampling wheel algorithm from udacity
	 * 
	 * @param in
	 * @return
	 */
	static List<Particle> resampling(List<Particle> in) {

		assert in.size() > 0;

		double maxWeight = 0;
		for (Particle p : in) {
			if (maxWeight < p.weight) {
				maxWeight = p.weight;
			}
		}

		List<Particle> out = new LinkedList<Particle>();

		Random rand = new Random();
		double beta = 0;
		int index = rand.nextInt(in.size());

		for (int i = 0; i < in.size(); i++) {
			beta += rand.nextDouble() * 2 * maxWeight;
			while (beta > in.get(index).weight) {
				beta -= in.get(index).weight;
				index = (index + 1) % in.size();
			}
			out.add(in.get(index));
		}

		return out;

	}

	/**
	 * Normalize the weights of the particles
	 * 
	 * @param parts
	 * @return
	 */
	static List<Particle> normalize(List<Particle> parts) {
		assert parts.size() > 0;

		double totalWeight = 0;
		for (Particle p : parts) {
			totalWeight += p.weight;
		}

		Set<Particle> deduplicates = new HashSet<>();
		deduplicates.addAll(parts);
		for (Particle p : deduplicates) {
			p.weight /= totalWeight;
		}

		return parts;
	}

}
