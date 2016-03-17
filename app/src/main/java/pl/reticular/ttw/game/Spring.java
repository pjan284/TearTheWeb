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
import android.graphics.RectF;

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.game.graph.Edge;
import pl.reticular.ttw.game.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Spring extends Edge {
	private float defaultLength;
	private Particle particle1, particle2;

	private Paint paint;

	private static final float tearFactor = 3.5f;
	private static final float tensionFactor = 0.9f;

	public enum Keys {
		DefaultLength
	}

	public class BrokenException extends Exception {
	}

	public Spring(Web web, Particle particle1, Particle particle2) {
		super(web, particle1, particle2);
		this.particle1 = (Particle) node1;
		this.particle2 = (Particle) node2;

		defaultLength = length() * tensionFactor;

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setColor(Color.GRAY);
	}

	public Spring(Web web, JSONObject json) throws JSONException {
		super(web, json);
		this.particle1 = (Particle) node1;
		this.particle2 = (Particle) node2;

		defaultLength = (float) json.getDouble(Keys.DefaultLength.toString());

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setColor(Color.GRAY);
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

		if (currentLength < defaultLength) {
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

	public void draw(Canvas canvas, float scale) {
		Vector2 p1 = particle1.getPos();
		Vector2 p2 = particle2.getPos();

		canvas.drawLine(p1.X * scale, p1.Y * scale, p2.X * scale, p2.Y * scale, paint);
	}

	public boolean scissors(float px, float py, float sense) {
		return distanceToPoint(px, py) <= sense;
	}

	public float distanceToPoint(float px, float py) {
		Vector2 p1 = particle1.getPos();
		Vector2 p2 = particle2.getPos();
		Vector2 p3;
		float A = p2.X - p1.X;
		float B = p2.Y - p1.Y;

		float u = (float) ((A * (px - p1.X) + B * (py - p1.Y)) / (Math.pow(A, 2) + Math.pow(B, 2)));
		if (u <= 0) {
			p3 = p1;
		} else if (u >= 1) {
			p3 = p2;
		} else {
			p3 = new Vector2(p1.X + u * A, p1.Y + u * B);
		}
		return (float) Math.sqrt(Math.pow(px - p3.X, 2) + Math.pow(py - p3.Y, 2));
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