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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.reticular.ttw.game.model.web.graph.Edge;
import pl.reticular.ttw.game.model.web.graph.Graph;
import pl.reticular.ttw.game.model.web.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class WebFactory {
	private static WebFactory instance = new WebFactory();

	public static WebFactory getInstance() {
		return instance;
	}

	private WebFactory() {
	}

	public Web recreate(JSONObject json) throws JSONException {
		ArrayList<Node> particles = new ArrayList<>();
		ArrayList<Edge> springs = new ArrayList<>();
		Map<Integer, Node> nodeMap = new HashMap<>();

		JSONArray particlesStates = json.getJSONArray(Graph.Keys.Nodes.toString());
		JSONArray springsStates = json.getJSONArray(Graph.Keys.Edges.toString());

		for (int i = 0; i < particlesStates.length(); i++) {
			JSONObject particleState = particlesStates.getJSONObject(i);
			Particle particle = new Particle(particleState);
			particles.add(particle);
			nodeMap.put(particle.getId(), particle);
		}

		for (int i = 0; i < springsStates.length(); i++) {
			JSONObject springState = springsStates.getJSONObject(i);
			Spring spring = new Spring(nodeMap, springState);
			springs.add(spring);
		}

		return new Web(particles, springs);
	}

	public Web create(WebType webType) {
		switch (webType) {
			case Round5x6:
				return createRoundWeb(0, 0, 0.1f, 0.9f, 5, 6, 0.1f);
			case Round4x7:
				return createRoundWeb(0, 0, 0.1f, 0.9f, 4, 7, 0.1f);
			case Round5x7:
				return createRoundWeb(0, 0, 0.1f, 0.9f, 5, 7, 0.1f);
			case Round4x8:
				return createRoundWeb(0, 0, 0.1f, 0.9f, 4, 8, 0.1f);
			case Rect5x5:
				return createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 5, 5, 0.1f);
			case Rect6x6:
				return createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 6, 6, 0.1f);
			case Rect7x7:
				return createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 7, 7, 0.1f);
			case Rect8x8:
				return createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 8, 8, 0.1f);
			default:
				return createRoundWeb(0, 0, 0.1f, 0.9f, 5, 6, 0.1f);
		}
	}

	private Web createRectWeb(float left, float top, float right, float bottom, int xGaps, int yGaps, float spacing) {
		ArrayList<Node> particles = new ArrayList<>();
		ArrayList<Edge> springs = new ArrayList<>();

		float width = right - left;
		float height = bottom - top;

		float xs = width / xGaps;
		float ys = height / yGaps;

		Particle previousKeyParticles[] = null;

		for (int y = 0; y <= yGaps; y++) {

			Particle keyParticles[] = new Particle[xGaps + 1];

			for (int x = 0; x <= xGaps; x++) {
				boolean pinned = (x == 0 || y == 0 || x == xGaps || y == xGaps);

				Particle p = new Particle(left + (x * xs), top + (y * ys), pinned);
				particles.add(p);
				keyParticles[x] = p;

				// horizontal chain
				if (x > 0 && y != 0 && y != yGaps) {
					addChain(particles, springs, p, keyParticles[x - 1], spacing);
				}

				// vertical chain
				if (y > 0 && x != 0 && x != xGaps) {
					addChain(particles, springs, p, previousKeyParticles[x], spacing);
				}
			}

			previousKeyParticles = keyParticles;
		}

		return new Web(particles, springs);
	}

	private Web createRoundWeb(float xs, float ys, float rMin, float rMax, int cylinders, int sectors, float spacing) {
		ArrayList<Node> particles = new ArrayList<>();
		ArrayList<Edge> springs = new ArrayList<>();

		float baseAngle = (float) Math.PI * (-0.5f + 1.0f / sectors);

		Particle previousKeyParticles[] = null;

		for (int c = 0; c < cylinders; c++) {

			Particle keyParticles[] = new Particle[sectors];

			for (int s = 0; s < sectors; s++) {
				boolean pinned = (c == cylinders - 1);

				float angle = baseAngle + s * ((float) Math.PI * 2.0f / sectors);
				float r = rMin + c * ((rMax - rMin) / (cylinders - 1));

				Particle p = new Particle(xs + r * (float) Math.cos(angle),
						ys + r * (float) Math.sin(angle),
						pinned);
				particles.add(p);
				keyParticles[s] = p;

				// round chain
				if (s > 0) {
					addChain(particles, springs, p, keyParticles[s - 1], spacing);
				}

				// radial chain
				if (c > 0) {
					addChain(particles, springs, p, previousKeyParticles[s], spacing);
				}
			}

			// finnish round chain
			addChain(particles, springs, keyParticles[sectors - 1], keyParticles[0], spacing);

			previousKeyParticles = keyParticles;
		}

		return new Web(particles, springs);
	}

	private void addChain(ArrayList<Node> particles, ArrayList<Edge> springs, Particle start, Particle end, float segmentLength) {
		Vector2 p1 = start.getPos();
		Vector2 p2 = end.getPos();
		float length = Vector2.sub(p1, p2).length();

		int segments = (int) Math.ceil(length / segmentLength);

		for (int i = 1; i < segments; i++) {
			Vector2 p = Vector2.lerp(p1, p2, (float) i / (float) segments);
			Particle part = new Particle(p.X, p.Y, false);
			particles.add(part);

			Spring spring = new Spring(start, part);
			springs.add(spring);

			start = part;
		}

		Spring spring = new Spring(start, end);
		springs.add(spring);
	}
}
