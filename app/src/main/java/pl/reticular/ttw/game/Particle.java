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

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.game.graph.Node;
import pl.reticular.ttw.utils.Vector2;

public class Particle extends Node {
	private Vector2 pos, prevPos;
	private boolean pinned;
	private Paint paint;

	private static final float dampingFactor = 0.99f;

	private enum Keys {
		Pos,
		PrevPos,
		Pinned
	}

	public Particle(float x, float y, boolean pinned) {
		super();
		pos = new Vector2(x, y);
		prevPos = new Vector2(x, y);
		this.pinned = pinned;

		paint = new Paint();
		paint.setAntiAlias(true);
		resetColor();
	}

	public Particle(JSONObject json) throws JSONException {
		super();
		pos = new Vector2(json.getJSONObject(Keys.Pos.toString()));
		prevPos = new Vector2(json.getJSONObject(Keys.PrevPos.toString()));
		pinned = json.getBoolean(Keys.Pinned.toString());

		paint = new Paint();
		paint.setAntiAlias(true);
		resetColor();
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put(Keys.Pos.toString(), pos.toJSON());
		state.put(Keys.PrevPos.toString(), prevPos.toJSON());
		state.put(Keys.Pinned.toString(), pinned);

		return state;
	}

	public void setColor(int color) {
		paint.setColor(color);
	}

	public void resetColor() {
		if (pinned) {
			paint.setColor(Color.BLUE);
		} else {
			paint.setColor(Color.GREEN);
		}
	}

	public void draw(Canvas canvas, float scale) {
		if (isPinned()) {
			canvas.drawCircle(pos.X * scale, pos.Y * scale, 3.0f, paint);
		}
	}

	public void update(float dt, Vector2 gravity) {
		if (!pinned) {

			// Position Verlet integration method
			float nx, ny;
			nx = pos.X + (pos.X - prevPos.X) * dampingFactor + gravity.X * dt * dt;
			ny = pos.Y + (pos.Y - prevPos.Y) * dampingFactor + gravity.Y * dt * dt;

			prevPos.set(pos);

			pos.set(nx, ny);

			// Simple Euler integration method
			//velocity.add(Vector2.scale(gravity, dt));
			//position.add(Vector2.scale(velocity, dt));
		} else {
			pos.set(prevPos);
		}
	}

	public Vector2 getPos() {
		return pos;
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	public void setPinnedPos(Vector2 pos) {
		this.pos.set(pos);
		prevPos.set(pos);
	}
}
