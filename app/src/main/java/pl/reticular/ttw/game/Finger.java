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

import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Finger implements Savable {
	private boolean bitten;
	private float timeToHeal;

	private Paint paint;
	private Vector2 position;
	private float radius;
	private Particle selectedParticle;

	private static final float healTime = 2.0f;

	private enum Keys {
		Bitten,
		TimeToHeal
	}

	Finger() {
		bitten = false;
		timeToHeal = 0.0f;

		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		position = null;
		radius = 0.1f;
		selectedParticle = null;

		setBitten(false);
	}

	public Finger(JSONObject json) throws JSONException {
		bitten = json.getBoolean(Keys.Bitten.toString());
		timeToHeal = (float) json.getDouble(Keys.TimeToHeal.toString());

		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		position = null;
		radius = 0.1f;
		selectedParticle = null;

		setBitten(false);
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put(Keys.Bitten.toString(), bitten);
		state.put(Keys.TimeToHeal.toString(), timeToHeal);

		return state;
	}

	public void draw(Canvas canvas, float scale) {
		if (position != null) {
			canvas.drawCircle(position.X * scale, position.Y * scale, radius * scale, paint);
		}
	}

	public void update(float dt) {
		if (bitten) {
			timeToHeal -= dt;

			if (timeToHeal <= 0) {
				setBitten(false);
			}
		}
	}

	public void setBitten(boolean bitten) {
		this.bitten = bitten;
		if (this.bitten) {
			paint.setColor(Color.RED);
			timeToHeal = healTime;
			cancelDragging();
		} else {
			paint.setColor(Color.YELLOW);
		}
	}

	public boolean isInContactWith(Spider spider) {
		return position != null && Vector2.length(Vector2.sub(position, spider.getPosition())) < radius;
	}

	public boolean isBitten() {
		return bitten;
	}

	public void startTracking(Vector2 touch, Web web, SpiderManager spiderManager) {
		position = touch;
		if (!bitten) {
			Particle particle = web.selectParticleInRange(touch, radius);
			if (particle != null) {
				spiderManager.onParticlePulled(particle);
				selectedParticle = particle;
				selectedParticle.setPinned(true);
			}
		}
	}

	public void continueTracking(Vector2 touch) {
		if (position != null) {
			if (selectedParticle != null) {
				Vector2 move = Vector2.sub(touch, position);
				Vector2 particlePos = selectedParticle.getPos();
				particlePos.add(move);
				selectedParticle.setPinnedPos(particlePos);
			}
			position.set(touch);
		}
	}

	public void cancelTracking() {
		cancelDragging();
		position = null;
	}

	public void cancelDragging() {
		if (selectedParticle != null) {
			selectedParticle.setPinned(false);
			selectedParticle = null;
		}
	}
}