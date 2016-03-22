package pl.reticular.ttw.game.model.graph;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import pl.reticular.ttw.utils.Savable;

public class Node implements Savable {
	protected LinkedList<Edge> edges;

	public Node() {
		edges = new LinkedList<>();
	}

	public Node(JSONObject json) throws JSONException {
		edges = new LinkedList<>();
	}


	@Override
	public JSONObject toJSON() throws JSONException {
		return new JSONObject();
	}

	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	public void removeEdge(Edge edge) {
		edges.remove(edge);
	}

	public LinkedList<Edge> getEdges() {
		return edges;
	}

	public Edge getEdgeTo(Node v2) {
		for (Edge e : edges) {
			if (e.next(this) == v2) {
				return e;
			}
		}
		return null;
	}
}
