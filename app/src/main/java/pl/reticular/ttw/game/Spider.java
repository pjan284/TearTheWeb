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
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.LinkedList;
import java.util.Random;

import pl.reticular.ttw.game.graph.Edge;
import pl.reticular.ttw.game.graph.Graph;
import pl.reticular.ttw.game.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Spider {
	int mode;
	Graph graph;

	/**
	 * Spring on which spider sits
	 */
	Spring spring;

	/**
	 * Spider position on spring
	 */
	float springPercent;

	/**
	 * End of the spring towards which spider is currently going.
	 */
	Particle target;

	static final float SPEED_NORMAL = 0.25f;
	static final float SPEED_FURIOUS = 0.5f;

	Paint paint;

	Random generator;

	/**
	 * Walks randomly
	 */
	public static final int MODE_RANDOM = 0;

	/**
	 * Walks through path to a place where finger is
	 */
	public static final int MODE_ATTACK = 1;

	/**
	 * Lost spring and falling down
	 */
	public static final int MODE_FALLING = 2;

	private LinkedList<Node> path;

	private Vector2 position;
	private Vector2 velocity;
	private float rotation;

	private static float legs[] = {
			0, 0, 12, -8,
			12, -8, 16, -4,

			0, 0, 12, -4,
			12, -4, 16, 0,

			0, 0, 12, 0,
			12, 0, 16, 4,

			0, 0, 12, 4,
			12, 4, 16, 8,

			0, 0, -12, -8,
			-12, -8, -16, -4,

			0, 0, -12, -4,
			-12, -4, -16, 0,

			0, 0, -12, 0,
			-12, 0, -16, 4,

			0, 0, -12, 4,
			-12, 4, -16, 8,
	};

	private static Vector2 upVector = new Vector2(0.0f, 1.0f);

	Spider(Graph graph) {
		this.graph = graph;

		spring = (Spring) graph.getRandomEdge();
		target = spring.getParticle2();

		mode = MODE_RANDOM;

		paint = new Paint();
		paint.setColor(Color.rgb(255, 0, 0));
		paint.setStrokeWidth(2.0f);

		generator = new Random();

		path = null;

		position = new Vector2();
		rotation = 0;
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

	void findNextTarget() {
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

	void draw(Canvas canvas, float scale) {
		float x = position.X * scale;
		float y = position.Y * scale;

		canvas.save();
		canvas.translate(x, y);
		canvas.rotate(-rotation, 0, 0);

		canvas.drawCircle(0, 0, 8.0f, paint);

		canvas.drawLines(legs, 0, legs.length, paint);

		canvas.restore();
	}

	private float moduloDistance(float a, float b, float modulo) {
		if (a < b) {
			float diff1 = b - a;
			float diff2 = a + (modulo - b);
			if (diff1 < diff2) {
				return -diff1;
			} else {
				return diff2;
			}
		} else {
			float diff1 = a - b;
			float diff2 = b + (modulo - a);
			if (diff1 < diff2) {
				return diff1;
			} else {
				return -diff2;
			}
		}
	}

	void update(float dt, Vector2 gravity) {
		if (mode != MODE_FALLING) {
			float len = spring.length();

			float speed = SPEED_NORMAL;
			if (mode == MODE_ATTACK) {
				speed = SPEED_FURIOUS;
			}

			springPercent += (speed * dt) / len;

			if (springPercent >= 1.0f) {
				findNextTarget();
				springPercent = 0.0f;
			}

			Vector2 newPosition = spring.getInterpolatedPosition(springPercent, target);
			velocity = Vector2.scale(Vector2.sub(newPosition, position), 1 / dt);
			position = newPosition;

			float desiredRot = (float) (spring.getAngle(upVector, target) * 180 / Math.PI);

			float diffRot = moduloDistance(desiredRot, rotation, 360.0f);

			rotation += diffRot / 10.0f;
		} else {
			velocity.add(Vector2.scale(gravity, dt));
			position.add(Vector2.scale(velocity, dt));
		}
	}

	public Vector2 getPosition() {
		return position;
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
		LinkedList<Node> newPath = graph.findPathToNode(target, fingerNode);
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

	public void onEdgeRemoved(Edge edge) {
		switch (mode) {
			case MODE_RANDOM:
				if (edge == spring) {
					switchToFalling();
				}
				break;
			case MODE_ATTACK:
				if (edge == spring) {
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
