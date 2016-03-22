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

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.game.model.meta.MetaData;
import pl.reticular.ttw.game.model.meta.MetaDataHelper;
import pl.reticular.ttw.game.model.webs.WebFactory;
import pl.reticular.ttw.game.model.webs.WebType;
import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Game implements Savable {

	private enum Keys {
		Web,
		Finger,
		SpiderManager,
		Meta
	}

	private Finger finger;

	private Web web;

	private MetaDataHelper metaDataHelper;

	private SpiderSet spiderSet;

	public Game() {
		web = WebFactory.createWeb(WebType.Round5x6);
		finger = new Finger();
		metaDataHelper = new MetaDataHelper();
		spiderSet = new SpiderSet();
		spiderSet.populate(metaDataHelper.getMetaData().getLevel(), web);
	}

	public Game(JSONObject json) throws JSONException {

		metaDataHelper = new MetaDataHelper(new MetaData(json.getJSONObject(Keys.Meta.toString())));

		web = new Web(json.getJSONObject(Keys.Web.toString()));

		finger = new Finger(json.getJSONObject(Keys.Finger.toString()));

		spiderSet = new SpiderSet(json.getJSONObject(Keys.SpiderManager.toString()), web);
	}

	@Override
	public JSONObject toJSON() throws JSONException {

		JSONObject state = new JSONObject();

		state.put(Keys.Web.toString(), web.toJSON());
		state.put(Keys.Finger.toString(), finger.toJSON());

		state.put(Keys.SpiderManager.toString(), spiderSet.toJSON());

		state.put(Keys.Meta.toString(), metaDataHelper.getMetaData().toJSON());

		return state;
	}

	public boolean isFinished() {
		return metaDataHelper.getMetaData().isFinished();
	}

	public MetaDataHelper getMetaDataHelper() {
		return metaDataHelper;
	}

	public MetaData getMetaData() {
		return metaDataHelper.getMetaData();
	}

	public Web getWeb() {
		return web;
	}

	public void setWeb(Web web) {
		this.web = web;
	}

	public SpiderSet getSpiderSet() {
		return spiderSet;
	}

	public Finger getFinger() {
		return finger;
	}

	public void update(float dt, Vector2 gravity, RectF gameArea) {
		web.update(dt, gravity, gameArea);

		spiderSet.update(dt, gravity, gameArea);

		if (spiderSet.getNumSpiders() == 0) {
			metaDataHelper.levelUp();
		}

		finger.update(dt);

		//check game over conditions
		if (!isFinished() && !finger.isBitten()) {
			if (spiderSet.areAnyInContactWith(finger)) {
				finger.setBitten(true);
				metaDataHelper.die();
			}
		}
	}
}