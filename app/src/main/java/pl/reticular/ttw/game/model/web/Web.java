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

import java.util.ArrayList;
import java.util.Iterator;

import pl.reticular.ttw.game.model.web.graph.Edge;
import pl.reticular.ttw.game.model.web.graph.Graph;
import pl.reticular.ttw.game.model.web.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Web extends Graph {

	public interface WebObserver {
		void onSpringBroken(Spring spring);

		void onSpringOut(Spring spring);
	}

	protected final int physicsAccuracy = 1;

	private WebObserver observer;

	public Web(ArrayList<Node> particles, ArrayList<Edge> springs) {
		super(particles, springs);
	}

	public JSONObject toJSON() throws JSONException {
		return super.toJSON();
	}

	public void update(float dt, Vector2 gravity, RectF gameArea) {
		// Resolve springs and remove broken
		for (int i = 0; i < physicsAccuracy; i++) {
			Iterator<Edge> it = edges.iterator();
			while (it.hasNext()) {
				Spring spring = (Spring) it.next();
				try {
					spring.resolveVerlet();
				} catch (Spring.BrokenException e) {
					observer.onSpringBroken(spring);
					onRemoveEdge(spring);
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
			Spring spring = (Spring) it.next();
			if (spring.isOut(gameArea)) {
				observer.onSpringOut(spring);
				onRemoveEdge(spring);
				it.remove();
			}
		}
	}

	public Particle selectParticleInRange(Vector2 clickPos, float r) {
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

	public void setObserver(WebObserver wo) {
		observer = wo;
	}
}