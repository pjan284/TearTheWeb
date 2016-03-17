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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pl.reticular.ttw.utils.Savable;

public abstract class Graph implements Savable {
	protected ArrayList<Node> nodes;
	protected ArrayList<Edge> edges;

	private enum Keys {
		Nodes,
		Edges
	}

	public Graph() {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
	}

	public Graph(JSONObject json) throws JSONException {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();

		JSONArray nodeStates = json.getJSONArray(Keys.Nodes.toString());
		JSONArray edgeStates = json.getJSONArray(Keys.Edges.toString());

		for (int i = 0; i < nodeStates.length(); i++) {
			JSONObject nodeState = nodeStates.getJSONObject(i);
			Node node = recreateNode(nodeState);
			nodes.add(node);
		}

		for (int i = 0; i < edgeStates.length(); i++) {
			JSONObject edgeState = edgeStates.getJSONObject(i);
			Edge edge = recreateEdge(edgeState);
			edges.add(edge);
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		JSONArray nodeStates = new JSONArray();
		JSONArray edgeStates = new JSONArray();

		for (Node node : nodes) {
			JSONObject nodeState = node.toJSON();
			nodeStates.put(nodeState);
		}

		for (Edge edge : edges) {
			JSONObject edgeState = edge.toJSON();
			edgeStates.put(edgeState);
		}

		state.put(Keys.Nodes.toString(), nodeStates);
		state.put(Keys.Edges.toString(), edgeStates);

		return state;
	}

	protected abstract Node recreateNode(JSONObject state) throws JSONException;

	protected abstract Edge recreateEdge(JSONObject state) throws JSONException;

	public void onRemoveEdge(Edge edge) {
		Node n1 = edge.getNode1();
		n1.removeEdge(edge);
		if (n1.getEdges().size() == 0) {
			nodes.remove(n1);
		}

		Node n2 = edge.getNode2();
		n2.removeEdge(edge);
		if (n2.getEdges().size() == 0) {
			nodes.remove(n2);
		}
	}

	public Edge getRandomEdge() {
		int i = (int) Math.floor(Math.random() * edges.size());
		return edges.get(i);
	}

	protected void insert(Edge e) {
		edges.add(e);
	}

	protected void insert(Node v) {
		nodes.add(v);
	}

	public void print() {
		Log.d(getClass().getName(), "Graph:");
		for (int i = 0; i < nodes.size(); i++) {
			String str = i + ": " + printList(nodes.get(i));
			Log.d(getClass().getName(), str);
		}
	}

	private String printList(Node node) {
		String str = "";
		for (Edge e : node.edges) {
			str += nodes.indexOf(e.next(node));
			str += ", ";
		}
		return str;
	}

	private LinkedList<Node> previousToPath(Node found, HashMap<Node, Node> previous) {
		LinkedList<Node> path = new LinkedList<>();
		while (found != null) {
			path.addFirst(found);
			found = previous.get(found);
		}
		return path;
	}

	/**
	 * Non-optimal implementation of BFS
	 *
	 * @param start    start node
	 * @param toFind   end node
	 * @param maxLevel max hops number or -1
	 * @return List of Nodes from start (included) to toFind (included) if path exists or
	 * null if path doesn't exist or has 0 elements
	 */
	public LinkedList<Node> findPathToNode(Node start, Node toFind, int maxLevel) {
		if (start == toFind || toFind == null || start == null) {
			return null;
		}
		HashMap<Node, Node> previous = new HashMap<>();
		HashMap<Node, Integer> levels = new HashMap<>();
		LinkedList<Node> queue = new LinkedList<>();
		queue.add(start);
		previous.put(start, null);
		levels.put(start, 0);
		while (!queue.isEmpty()) {
			Node v = queue.remove();
			if (v == toFind) {
				return previousToPath(v, previous);
			}

			int level = levels.get(v);
			if (level > maxLevel && maxLevel != -1) {
				return null;
			}

			for (Edge e : v.getEdges()) {
				Node next = e.next(v);
				if (!previous.containsKey(next)) {
					queue.add(next);
					previous.put(next, v);
					levels.put(next, level + 1);
				}
			}
		}
		return null;
	}

	public int getIndexOfNode(Node node) {
		return nodes.indexOf(node);
	}

	public Node getNode(int index) {
		return nodes.get(index);
	}
}
