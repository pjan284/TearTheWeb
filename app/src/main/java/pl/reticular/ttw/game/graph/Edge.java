package pl.reticular.ttw.game.graph;

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

import pl.reticular.ttw.utils.Savable;

public class Edge implements Savable {
	private Graph graph;

	protected Node node1, node2;

	public enum Keys {
		Node1,
		Node2
	}

	public Edge(Graph graph, Node v1, Node v2) {
		this.graph = graph;

		node1 = v1;
		node2 = v2;

		node1.addEdge(this);
		node2.addEdge(this);
	}

	public Edge(Graph graph, JSONObject json) throws JSONException {
		this.graph = graph;

		node1 = graph.getNode(json.getInt(Keys.Node1.toString()));
		node2 = graph.getNode(json.getInt(Keys.Node2.toString()));

		node1.addEdge(this);
		node2.addEdge(this);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(Keys.Node1.toString(), graph.getIndexOfNode(node1));
		json.put(Keys.Node2.toString(), graph.getIndexOfNode(node2));

		return json;
	}

	public Node next(Node prev) {
		if (prev == node1) {
			return node2;
		} else {
			return node1;
		}
	}

	public Node getNode1() {
		return node1;
	}

	public Node getNode2() {
		return node2;
	}
}
