package pl.reticular.ttw.utils;

import org.json.JSONException;
import org.json.JSONObject;

public interface Factory<T> {
	T fromJson(JSONObject json) throws JSONException;
}
