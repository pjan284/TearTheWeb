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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Settings {
	private static Settings instance = new Settings();

	public static Settings getInstance() {
		return instance;
	}

	private Settings() {
	}

	private static final String SETTINGS_NAME = "Settings";

	private enum Keys {
		LastGame,
		HighScores
	}

	private enum HighScoreKeys {
		Date,
		Score
	}

	public JSONObject getLastGame(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		String string = prefs.getString(Keys.LastGame.toString(), "{}");
		JSONObject json = new JSONObject();
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public void saveLastGame(Context context, JSONObject json) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);

		String string = json.toString();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.LastGame.toString(), string);
		editor.commit();
	}

	public void clearLastGame(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(Keys.LastGame.toString());
		editor.commit();
	}

	public boolean hasLastGame(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		return prefs.contains(Keys.LastGame.toString());
	}

	public List<Pair<Long, Integer>> getHighScores(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		String string = prefs.getString(Keys.HighScores.toString(), "[]");
		List<Pair<Long, Integer>> list = new LinkedList<>();
		try {
			JSONArray array = new JSONArray(string);
			for (int i = 0; i < array.length(); i += 1) {
				JSONObject json = array.getJSONObject(i);
				Long date = json.getLong(HighScoreKeys.Date.toString());
				Integer score = json.getInt(HighScoreKeys.Score.toString());
				list.add(new Pair<>(date, score));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void saveHighScore(Context context, int score) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		List<Pair<Long, Integer>> list = getHighScores(context);
		list.add(new Pair<>(System.currentTimeMillis(), score));
		JSONArray array = new JSONArray();
		try {
			for (Pair<Long, Integer> item : list) {
				JSONObject json = new JSONObject();
				json.put(HighScoreKeys.Date.toString(), item.first);
				json.put(HighScoreKeys.Score.toString(), item.second);
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.HighScores.toString(), array.toString());
		editor.commit();
	}

	public void clearHighScores(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
		JSONArray array = new JSONArray();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Keys.HighScores.toString(), array.toString());
		editor.commit();
	}
}
