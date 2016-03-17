package pl.reticular.ttw.utils;

import org.json.JSONException;
import org.json.JSONObject;

public interface Savable {
	JSONObject toJSON() throws JSONException;
}
