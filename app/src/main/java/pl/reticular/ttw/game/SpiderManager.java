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
import android.graphics.RectF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class SpiderManager implements Savable {

	public interface SpiderObserver {
		void onSpiderOut(Spider spider);
	}

	private SpiderObserver observer;

	private LinkedList<Spider> spiders;

	private enum Keys {
		Spiders,
	}

	public SpiderManager(SpiderObserver observer) {
		this.observer = observer;
		spiders = new LinkedList<>();
	}

	public SpiderManager(JSONObject json, Web web, SpiderObserver observer) throws JSONException {
		this.observer = observer;
		spiders = new LinkedList<>();
		JSONArray spidersData = json.getJSONArray(Keys.Spiders.toString());
		for (int i = 0; i < spidersData.length(); i++) {
			JSONObject spiderState = spidersData.getJSONObject(i);
			Spider spider = new Spider(web, spiderState);
			spiders.add(spider);
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();
		JSONArray spidersData = new JSONArray();

		for (Spider spider : spiders) {
			JSONObject spiderState = spider.toJSON();
			spidersData.put(spiderState);
		}

		state.put(Keys.Spiders.toString(), spidersData);
		return state;
	}

	public void populate(int number, Web web) {
		spiders.clear();
		for (int i = 0; i < number; i++) {
			spiders.add(new Spider(web));
		}
	}

	public int getNumSpiders() {
		return spiders.size();
	}

	public void onParticlePulled(Particle pulled) {
		for (Spider spider : spiders) {
			spider.onParticlePulled(pulled);
		}
	}

	public void onSpringUnAvailable(Spring spring) {
		for (Spider spider : spiders) {
			spider.onSpringUnAvailable(spring);
		}
	}

	public void draw(Canvas canvas, float canvasScale) {
		for (Spider spider : spiders) {
			spider.draw(canvas, canvasScale);
		}
	}

	public void update(float dt, Vector2 gravity, RectF gameArea) {
		Iterator<Spider> it = spiders.iterator();
		while (it.hasNext()) {
			Spider spider = it.next();
			try {
				spider.update(dt, gravity, gameArea);
			} catch (Spider.OutException e) {
				observer.onSpiderOut(spider);
				it.remove();
			}
		}
	}

	public boolean areAnyInContactWith(Finger finger) {
		for (Spider spider : spiders) {
			if (finger.isInContactWith(spider)) {
				return true;
			}
		}

		return false;
	}
}
