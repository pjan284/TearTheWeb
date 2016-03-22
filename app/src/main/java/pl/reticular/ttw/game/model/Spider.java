package pl.reticular.ttw.game.model;

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
import android.support.v4.util.Pair;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Random;

import pl.reticular.ttw.game.model.graph.Edge;
import pl.reticular.ttw.game.model.graph.Graph;
import pl.reticular.ttw.game.model.graph.Node;
import pl.reticular.ttw.utils.Modulo;
import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Spider implements Savable {
	private int mode;
	private Graph graph;

	/**
	 * Spring on which spider sits
	 */
	private Spring spring;

	/**
	 * Spider position on spring
	 */
	private float springPercent;

	/**
	 * End of the spring towards which spider is currently going.
	 */
	private Particle target;

	private static final float SPEED_NORMAL = 0.25f;
	private static final float SPEED_FURIOUS = 0.5f;

	private Random generator;

	/**
	 * Walks randomly
	 */
	private static final int MODE_RANDOM = 0;

	/**
	 * Walks through path to a place where finger is
	 */
	private static final int MODE_ATTACK = 1;

	/**
	 * Lost spring and falling down
	 */
	private static final int MODE_FALLING = 2;

	private LinkedList<Node> path;

	private static final int fingerDetectDistance = 15;

	private Vector2 position;
	private Vector2 velocity;
	private float rotation;

	private static Vector2 upVector = new Vector2(0.0f, 1.0f);

	private enum Keys {
		Mode,
		Target,
		PrevTarget,
		SpringPercent,
		Position,
		Velocity,
		Rotation
	}

	public class OutException extends Exception {
	}

	public Spider(Graph graph) {
		this.graph = graph;

		spring = (Spring) graph.getRandomEdge();
		target = spring.getParticle2();

		mode = MODE_RANDOM;

		generator = new Random();

		path = null;

		position = new Vector2();
		velocity = new Vector2();
		rotation = 0;
	}

	public Spider(Graph graph, JSONObject json) throws JSONException {
		this.graph = graph;

		mode = json.getInt(Keys.Mode.toString());
		if (mode != MODE_FALLING) {
			target = (Particle) graph.getNode(json.getInt(Keys.Target.toString()));
			Node prevTarget = graph.getNode(json.getInt(Keys.PrevTarget.toString()));
			spring = (Spring) prevTarget.getEdgeTo(target);
			springPercent = (float) json.getDouble(Keys.SpringPercent.toString());
			position = new Vector2();
			velocity = new Vector2();
		} else {
			position = new Vector2(json.getJSONObject(Keys.Position.toString()));
			velocity = new Vector2(json.getJSONObject(Keys.Velocity.toString()));
		}

		rotation = (float) json.getDouble(Keys.Rotation.toString());

		generator = new Random();

		// TODO
		path = null;
		if (mode == MODE_ATTACK) {
			mode = MODE_RANDOM;
		}
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put(Keys.Mode.toString(), mode);
		if (mode != MODE_FALLING) {
			state.put(Keys.Target.toString(), graph.getIndexOfNode(target));
			state.put(Keys.PrevTarget.toString(), graph.getIndexOfNode(spring.next(target)));
			state.put(Keys.SpringPercent.toString(), springPercent);
		} else {
			state.put(Keys.Position.toString(), position.toJSON());
			state.put(Keys.Velocity.toString(), velocity.toJSON());
		}
		state.put(Keys.Rotation.toString(), rotation);

		return state;
	}

	private Pair<Particle, Spring> nextTargetAtRandom() {

		LinkedList<Edge> list = new LinkedList<>(target.getEdges());
		if (list.size() > 1) {
			list.remove(spring);
		}
		int i = generator.nextInt(list.size());
		Spring nextSpring = (Spring) list.get(i);

		if (nextSpring.getParticle1() == target) {
			return new Pair<>(nextSpring.getParticle2(), nextSpring);
		} else {
			return new Pair<>(nextSpring.getParticle1(), nextSpring);
		}
	}

	private Pair<Particle, Spring> nextTargetOnPath() {
		Node nextTarget = path.removeFirst();

		Spring nextSpring = (Spring) target.getEdgeTo(nextTarget);
		if (nextSpring == null) {
			switchToRandom();
			return nextTargetAtRandom();
		}
		return new Pair<>((Particle) nextTarget, nextSpring);
	}

	private void findNextTarget() {
		Pair<Particle, Spring> pair = nextTargetAtRandom();

		if (mode == MODE_ATTACK) {
			if (path.isEmpty()) {
				switchToRandom();
			} else {
				pair = nextTargetOnPath();
			}
		}

		target = pair.first;
		spring = pair.second;
		springPercent = 0.0f;
	}

	public void update(float dt, Vector2 gravity, RectF gameArea) throws OutException {
		if (mode != MODE_FALLING) {
			float len = spring.length();

			float speed = SPEED_NORMAL;
			if (mode == MODE_ATTACK) {
				speed = SPEED_FURIOUS;
			}

			// Avoid committing suicide
			if (!target.getPos().isInBounds(gameArea)) {
				//reverse
				target = (Particle) spring.next(target);
				springPercent = 1.0f - springPercent;
				switchToRandom();
			}

			springPercent += (speed * dt) / len;

			if (springPercent >= 1.0f) {
				findNextTarget();
				springPercent = 0.0f;
			}

			Vector2 newPosition = spring.getInterpolatedPosition(springPercent, target);
			velocity = Vector2.scale(Vector2.sub(newPosition, position), 1 / dt);
			position = newPosition;

			float desiredRot = spring.getAngle(upVector, target);

			float diffRot = Modulo.angleDifference(rotation, desiredRot);

			rotation += diffRot / 10.0f;
		} else {
			velocity.add(Vector2.scale(gravity, dt));
			position.add(Vector2.scale(velocity, dt));
		}

		if (!position.isInBounds(gameArea)) {
			throw new OutException();
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	private void switchToRandom() {
		mode = MODE_RANDOM;
		path = null;
	}

	private void switchToFalling() {
		mode = MODE_FALLING;
		spring = null;
		target = null;
		path = null;
	}

	private void switchToAttack(Node fingerNode) {
		LinkedList<Node> newPath = graph.findPathToNode(target, fingerNode, fingerDetectDistance);
		if (newPath != null) {
			Node node = newPath.removeFirst();
			if (node != target) {
				Log.e(getClass().getName(), "Bad path");
			}
			path = newPath;
			mode = MODE_ATTACK;
		} else {
			switchToRandom();
		}
	}

	public void onSpringUnAvailable(Spring unAvailableSpring) {
		switch (mode) {
			case MODE_RANDOM:
				if (unAvailableSpring == spring) {
					switchToFalling();
				}
				break;
			case MODE_ATTACK:
				if (unAvailableSpring == spring) {
					switchToFalling();
				} else {
					//We need to change path, as old may be invalid
					if (path.isEmpty()) {
						switchToRandom();
					} else {
						switchToAttack(path.getLast());
					}
				}
				break;
			case MODE_FALLING:
				break;
		}
	}

	public void onParticlePulled(Particle particle) {
		switch (mode) {
			case MODE_RANDOM:
				switchToAttack(particle);
				break;
			case MODE_ATTACK:
				if (particle != target) {
					switchToAttack(particle);
				}
				break;
			case MODE_FALLING:
				break;
		}
	}
}
