package pl.reticular.ttw.game.meta;

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

import pl.reticular.ttw.utils.Savable;

public class MetaData implements Savable {

	private long date;
	private int level;
	private int lives;
	private int score;

	public enum Keys {
		Date,
		Level,
		Lives,
		Score,
	}

	public MetaData() {
		date = System.currentTimeMillis();
		level = 1;
		lives = 1;
		score = 0;
	}

	public MetaData(int level, int lives, int score) {
		this.date = System.currentTimeMillis();
		this.level = level;
		this.lives = lives;
		this.score = score;
	}

	public MetaData(JSONObject json) throws JSONException {
		date = json.getLong(Keys.Date.toString());
		level = json.getInt(Keys.Level.toString());
		lives = json.getInt(Keys.Lives.toString());
		score = json.getInt(Keys.Score.toString());
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(Keys.Date.toString(), date);
		json.put(Keys.Level.toString(), level);
		json.put(Keys.Lives.toString(), lives);
		json.put(Keys.Score.toString(), score);

		return json;
	}

	public Long getDate() {
		return date;
	}

	public int getLevel() {
		return level;
	}

	public int getLives() {
		return lives;
	}

	public int getScore() {
		return score;
	}

	public MetaData levelUp() {
		int newLevel = level + 1;
		return new MetaData(newLevel, newLevel, score);
	}

	public MetaData addScore(int add) {
		return new MetaData(level, lives, score + add * level);
	}

	public MetaData die() {
		return new MetaData(level, lives - 1, score);
	}

	public boolean isFinished() {
		return lives <= 0;
	}
}

