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

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.game.webs.WebType;
import pl.reticular.ttw.utils.Factory;
import pl.reticular.ttw.utils.Savable;

public class Result implements Savable, Comparable<Result> {

	long date;
	int level;
	int score;
	WebType webType;

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

	public static class ResultFactory implements Factory<Result> {
		@Override
		public Result fromJson(JSONObject json) throws JSONException {
			long date = json.getLong(Keys.Date.toString());

			int level = json.getInt(Keys.Level.toString());

			int score = json.getInt(Keys.Score.toString());

			WebType webType;
			try {
				webType = WebType.valueOf(json.getString(Keys.WebType.toString()));
			} catch (IllegalArgumentException e) {
				webType = WebType.Round4x8;
			}

			return new Result(date, level, score, webType);
		}
	}

	@Override
	public int compareTo(@NonNull Result another) {
		int diffScore = another.getScore() - getScore();
		if (diffScore == 0) {
			long diffDate = another.getDate() - getDate();
			if (diffDate == 0) {
				return 0;
			} else if (diffDate < 0) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return diffScore;
		}
	}
}

