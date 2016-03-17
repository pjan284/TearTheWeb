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

import java.util.LinkedList;

import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Finger implements Savable {
	private Paint paint;
	private float radius;
	private Vector2 pos;
	private boolean visible;
	private boolean poisoned;
	private float poisonedTimeLeft;

	private static final float poisonedTime = 1.0f;

	private enum Keys {
		Poisoned,
		PoisonedTimeLeft
	}

	Finger() {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		radius = 0.1f;
		pos = new Vector2();
		poisoned = false;
		poisonedTimeLeft = 0.0f;
		setVisible(false);
		setPoisoned(false);
	}

	public Finger(JSONObject json) throws JSONException {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		radius = 0.1f;
		pos = new Vector2();
		poisoned = json.getBoolean(Keys.Poisoned.toString());
		poisonedTimeLeft = (float) json.getDouble(Keys.PoisonedTimeLeft.toString());
		setVisible(false);
		setPoisoned(false);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put(Keys.Poisoned.toString(), poisoned);
		state.put(Keys.PoisonedTimeLeft.toString(), poisonedTimeLeft);

		return state;
	}

	public void draw(Canvas canvas, float scale) {
		if (visible) {
			canvas.drawCircle(pos.X * scale, pos.Y * scale, radius * scale, paint);
		}
	}

	public void update(float dt) {
		if (poisoned) {
			poisonedTimeLeft -= dt;

			if (poisonedTimeLeft <= 0) {
				setPoisoned(false);
			}
		}
	}

	public float getRadius() {
		return radius;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setPoisoned(boolean poisoned) {
		this.poisoned = poisoned;
		if (this.poisoned) {
			paint.setColor(Color.RED);
			poisonedTimeLeft = poisonedTime;
		} else {
			paint.setColor(Color.YELLOW);
		}
	}

	public boolean isFreshlyPoisoned(LinkedList<Spider> spiders) {
		if (!visible) {
			return false;
		}
		if (poisoned) {
			return false;   //Already poisoned
		}
		for (Spider spider : spiders) {
			if (Vector2.length(Vector2.sub(pos, spider.getPosition())) < radius) {
				setPoisoned(true);
				return true;
			}
		}
		return false;
	}

	public boolean isPoisoned() {
		return poisoned;
	}
}