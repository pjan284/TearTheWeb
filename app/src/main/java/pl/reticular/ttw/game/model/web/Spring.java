package pl.reticular.ttw.game.model.web;

/*
 * Copyright (C) 2016 Piotr Jankowski
 *
 * This file is part of Tear The Web.
 *
 * Tear The Web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Tear The Web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Tear The Web. If not, see <http://www.gnu.org/licenses/>.
 */

import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import pl.reticular.ttw.game.model.web.graph.Edge;
import pl.reticular.ttw.game.model.web.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Spring extends Edge {
	private float defaultLength;
	private Particle particle1, particle2;

	private static final float tearFactor = 3.5f;
	private static final float tensionFactor = 0.9f;

	public enum Keys {
		DefaultLength
	}

	public class BrokenException extends Exception {
	}

	public Spring(Particle particle1, Particle particle2) {
		super(particle1, particle2);
		this.particle1 = (Particle) node1;
		this.particle2 = (Particle) node2;

		defaultLength = length() * tensionFactor;
	}

	public Spring(Map<Integer, Node> nodeMap, JSONObject json) throws JSONException {
		super(nodeMap, json);
		this.particle1 = (Particle) node1;
		this.particle2 = (Particle) node2;

		defaultLength = (float) json.getDouble(Keys.DefaultLength.toString());
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = super.toJSON();

		state.put(Keys.DefaultLength.toString(), defaultLength);

		return state;
	}

	public float length() {
		Vector2 d = Vector2.sub(particle1.getPos(), particle2.getPos());
		return d.length();
	}

	public void resolveVerlet() throws BrokenException {
		Vector2 p1 = particle1.getPos();
		Vector2 p2 = particle2.getPos();

		float lx = p1.X - p2.X;
		float ly = p1.Y - p2.Y;

		float currentLength = Vector2.length(lx, ly);

		if (currentLength < defaultLength || currentLength == 0.0f) {
			return;
		}

		if (currentLength > defaultLength * tearFactor) {
			throw new BrokenException();
		}

		float diff = defaultLength - currentLength;

		lx /= currentLength;
		ly /= currentLength;

		float diffX = lx * diff * 0.5f;
		float diffY = ly * diff * 0.5f;

		//try to restore original length
		p1.add(diffX, diffY);
		p2.add(-diffX, -diffY);
	}

	public Vector2 getInterpolatedPosition(float a, Node end) {
		Vector2 p1 = particle1.getPos();
		Vector2 p2 = particle2.getPos();
		if (end == node2) {
			return Vector2.lerp(p1, p2, a);
		} else {
			return Vector2.lerp(p2, p1, a);
		}
	}

	public float getAngle(Vector2 base, Node end) {
		Vector2 p1 = particle1.getPos();
		Vector2 p2 = particle2.getPos();
		if (end == node2) {
			Vector2 v2 = Vector2.sub(p1, p2);
			return Vector2.angle(v2, base);
		} else {
			Vector2 v2 = Vector2.sub(p2, p1);
			return Vector2.angle(v2, base);
		}
	}

	public boolean isOut(RectF area) {
		return !(particle1.getPos().isInBounds(area) || particle2.getPos().isInBounds(area));
	}

	public Particle getParticle1() {
		return particle1;
	}

	public Particle getParticle2() {
		return particle2;
	}
}