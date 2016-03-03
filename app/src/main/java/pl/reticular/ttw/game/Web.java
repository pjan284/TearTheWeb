package pl.reticular.ttw.game;

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

import android.graphics.Canvas;
import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import pl.reticular.ttw.game.graph.Edge;
import pl.reticular.ttw.game.graph.Graph;
import pl.reticular.ttw.game.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Web extends Graph {

	protected final int physicsAccuracy = 1;

	Web() {
		super();
	}

	Web(JSONObject json) throws JSONException {
		super(json);
	}

	@Override
	protected Node recreateNode(JSONObject state) throws JSONException {
		return new Particle(state);
	}

	@Override
	protected Edge recreateEdge(JSONObject state) throws JSONException {
		return new Spring(this, state);
	}

	public JSONObject toJSON() throws JSONException {
		return super.toJSON();
	}

	protected void addChain(Particle part1, Particle part2, int segments) {
		Vector2 p1 = part1.getPos();
		Vector2 p2 = part2.getPos();
		Particle start = part1;
		Particle end = part2;
		for (int i = 1; i < segments; i++) {
			Vector2 p = Vector2.lerp(p1, p2, (float) i / (float) segments);
			Particle part = new Particle(p.X, p.Y, false);
			nodes.add(part);

			Spring spring = new Spring(this, start, part);
			edges.add(spring);

			start = part;
		}

		Spring spring = new Spring(this, start, end);
		edges.add(spring);
	}

	protected void addNormalizedChain(Particle part1, Particle part2, float segmentLength) {
		Vector2 p1 = part1.getPos();
		Vector2 p2 = part2.getPos();
		float length = Vector2.sub(p1, p2).length();

		addChain(part1, part2, (int) Math.ceil(length / segmentLength));
	}

	protected void update(float dt, Vector2 gravity, RectF gameArea) {
		// Resolve springs and remove broken
		for (int i = 0; i < physicsAccuracy; i++) {
			Iterator<Edge> it = edges.iterator();
			while (it.hasNext()) {
				Edge edge = it.next();
				try {
					((Spring) edge).resolveVerlet();
				} catch (Spring.BrokenException e) {
					onRemoveEdge(edge);
					it.remove();
				}
			}
		}

		// Update nodes (and springs) positions
		for (Node node : nodes) {
			((Particle) node).update(dt, gravity);
		}

		// Remove springs that fallen out of game area
		Iterator<Edge> it = edges.iterator();
		while (it.hasNext()) {
			Edge edge = it.next();
			if (((Spring) edge).isOut(gameArea)) {
				onRemoveEdge(edge);
				it.remove();
			}
		}
	}

	protected void draw(Canvas canvas, float scale) {
		for (Edge edge : edges) {
			((Spring) edge).draw(canvas, scale);
		}

		for (Node node : nodes) {
			((Particle) node).draw(canvas, scale);
		}
	}

	Particle selectParticleInRange(Vector2 clickPos, float r) {
		float minDistance = r;
		Particle chosen = null;
		for (Node node : nodes) {
			Particle p = (Particle) node;
			float d = Vector2.length(Vector2.sub(p.getPos(), clickPos));
			if (!p.isPinned() && d <= minDistance) {
				minDistance = d;
				chosen = p;
			}
		}

		return chosen;
	}
}