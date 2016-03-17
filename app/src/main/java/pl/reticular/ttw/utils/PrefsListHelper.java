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

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PrefsListHelper<T extends Savable> {
	Factory<T> factory;

	public PrefsListHelper(Factory<T> factory) {
		this.factory = factory;
	}

	public List<T> getList(SharedPreferences prefs, String key) {
		String string = prefs.getString(key, "[]");
		List<T> list = new LinkedList<>();
		try {
			JSONArray array = new JSONArray(string);
			for (int i = 0; i < array.length(); i += 1) {
				JSONObject json = array.getJSONObject(i);
				list.add(factory.fromJson(json));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			PrefsHelper.remove(prefs, key);
		}
		return list;
	}

	public void putList(SharedPreferences prefs, String key, List<T> list) {
		JSONArray array = new JSONArray();
		try {
			for (T item : list) {
				JSONObject json = item.toJSON();
				array.put(json);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			PrefsHelper.remove(prefs, key);
		}

		PrefsHelper.putString(prefs, key, array.toString());
	}
}
