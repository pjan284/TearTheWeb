package pl.reticular.ttw.utils;

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

public class Result implements Savable, Comparable<Result> {

	int score;
	long date;

	public enum Keys {
		Score,
		Date
	}

	public Result(int score, long date) {
		this.score = score;
		this.date = date;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(Keys.Score.toString(), score);
		json.put(Keys.Date.toString(), date);

		return json;
	}

	public int getScore() {
		return score;
	}

	public Long getDate() {
		return date;
	}

	public static class ResultFactory implements Factory<Result> {
		@Override
		public Result fromJson(JSONObject json) throws JSONException {
			return new Result(json.getInt(Keys.Score.toString()), json.getLong(Keys.Date.toString()));
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

