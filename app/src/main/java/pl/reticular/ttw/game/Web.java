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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import pl.reticular.ttw.game.graph.Edge;
import pl.reticular.ttw.game.graph.Graph;
import pl.reticular.ttw.game.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Web extends Graph {

	private enum Keys {
		Particles,
		Springs
	}

	protected final int physicsAccuracy = 1;

	Web() {
		super();
	}

	Web(JSONObject json) throws JSONException {
		super();

		JSONArray particles = json.getJSONArray(Keys.Particles.toString());
		JSONArray springs = json.getJSONArray(Keys.Springs.toString());

		for (int i = 0; i < particles.length(); i++) {
			JSONObject particleState = particles.getJSONObject(i);
			Particle part = new Particle(particleState);
			nodes.add(part);
		}

		for (int i = 0; i < springs.length(); i++) {
			JSONObject springState = springs.getJSONObject(i);
			Particle p1 = (Particle) nodes.get(springState.getInt(Spring.Keys.Node1.toString()));
			Particle p2 = (Particle) nodes.get(springState.getInt(Spring.Keys.Node2.toString()));
			float defaultLength = (float) springState.getDouble(Spring.Keys.DefaultLength.toString());

			Spring spring = new Spring(p1, p2, defaultLength);
			edges.add(spring);
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		JSONArray particles = new JSONArray();
		JSONArray springs = new JSONArray();

		Iterator iterator = nodes.iterator();
		while (iterator.hasNext()) {
			JSONObject particleState = ((Particle) iterator.next()).toJSON();
			particles.put(particleState);
		}

		iterator = edges.iterator();
		while (iterator.hasNext()) {
			Spring spring = (Spring) iterator.next();
			JSONObject springState = spring.toJSON();
			springState.put(Spring.Keys.Node1.toString(), nodes.indexOf(spring.getNode1()));
			springState.put(Spring.Keys.Node2.toString(), nodes.indexOf(spring.getNode2()));
			springs.put(springState);
		}

		state.put(Keys.Particles.toString(), particles);
		state.put(Keys.Springs.toString(), springs);

		return state;
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

			Spring spring = new Spring(start, part);
			edges.add(spring);

			start = part;
		}

		Spring spring = new Spring(start, end);
		edges.add(spring);
	}

	protected void addNormalizedChain(Particle part1, Particle part2, float segmentLength) {
		Vector2 p1 = part1.getPos();
		Vector2 p2 = part2.getPos();
		float length = Vector2.sub(p1, p2).length();

		addChain(part1, part2, (int) Math.ceil(length / segmentLength));
	}

	protected void update(float dt, Vector2 gravity) {
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

		for (Node node : nodes) {
			((Particle) node).update(dt, gravity);
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