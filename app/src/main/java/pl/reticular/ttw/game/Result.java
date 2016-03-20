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

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.game.webs.WebType;
import pl.reticular.ttw.utils.Savable;

public class Result implements Savable {

	private long date;
	private int level;
	private int score;
	private WebType webType;

	public enum Keys {
		Date,
		Level,
		Score,
		WebType
	}

	public Result(long date, int level, int score, WebType webType) {
		this.date = date;
		this.level = level;
		this.score = score;
		this.webType = webType;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(Keys.Date.toString(), date);
		json.put(Keys.Level.toString(), level);
		json.put(Keys.Score.toString(), score);
		json.put(Keys.WebType.toString(), webType.toString());

		return json;
	}

	public Long getDate() {
		return date;
	}

	public int getLevel() {
		return level;
	}

	public int getScore() {
		return score;
	}

	public WebType getWebType() {
		return webType;
	}
}

