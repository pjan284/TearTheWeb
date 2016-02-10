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

import org.json.JSONException;
import org.json.JSONObject;

public class Vector2 {
	public float X;
	public float Y;

	public Vector2() {
		X = 0.0f;
		Y = 0.0f;
	}

	public Vector2(Vector2 v) {
		X = v.X;
		Y = v.Y;
	}

	public Vector2(float x, float y) {
		X = x;
		Y = y;
	}

	public Vector2(JSONObject json) throws JSONException {
		X = (float) json.getDouble("X");
		Y = (float) json.getDouble("Y");
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject state = new JSONObject();

		state.put("X", (double) X);
		state.put("Y", (double) Y);

		return state;
	}

	public void set(float x, float y) {
		X = x;
		Y = y;
	}

	public void set(Vector2 v) {
		X = v.X;
		Y = v.Y;
	}

	public void add(float x, float y) {
		X += x;
		Y += y;
	}

	public void add(Vector2 v) {
		X += v.X;
		Y += v.Y;
	}

	public static Vector2 add(Vector2 v1, Vector2 v2) {
		Vector2 ret = new Vector2(v1);
		ret.add(v2);
		return ret;
	}

	public void sub(Vector2 v) {
		X -= v.X;
		Y -= v.Y;
	}

	public static Vector2 sub(Vector2 v1, Vector2 v2) {
		Vector2 ret = new Vector2(v1);
		ret.sub(v2);
		return ret;
	}

	public void scale(float a) {
		X *= a;
		Y *= a;
	}

	public static Vector2 scale(Vector2 v, float a) {
		return new Vector2(v.X * a, v.Y * a);
	}

	public void reverse() {
		X = -X;
		Y = -Y;
	}

	public static Vector2 reverse(Vector2 v) {
		return new Vector2(-v.X, -v.Y);
	}

	public float dotProduct(Vector2 v2) {
		return X * v2.X + Y * v2.Y;
	}

	public static float dotProduct(Vector2 v1, Vector2 v2) {
		return v1.X * v2.X + v1.Y * v2.Y;
	}

	public double angle(Vector2 v2) {
		double a = Math.atan2(v2.Y, v2.X) - Math.atan2(Y, X);
		if (a < 0) {
			a = a + 2 * Math.PI;
		}
		return a;
	}

	public static double angle(Vector2 v1, Vector2 v2) {
		double a = Math.atan2(v2.Y, v2.X) - Math.atan2(v1.Y, v1.X);
		if (a < 0) {
			a = a + 2 * Math.PI;
		}
		return a;
	}

	public float length() {
		return (float) Math.sqrt((double) (X * X + Y * Y));
	}

	public static float length(Vector2 v) {
		return (float) Math.sqrt((double) (v.X * v.X + v.Y * v.Y));
	}

	public static float length(float x, float y) {
		return (float) Math.sqrt((double) (x * x + y * y));
	}

	public void normalize() {
		float l = length();
		if (l != 0.0f) {
			X /= l;
			Y /= l;
		}
	}

	public static Vector2 lerp(Vector2 v1, Vector2 v2, float a) {
		return new Vector2(v1.X + (v2.X - v1.X) * a, v1.Y + (v2.Y - v1.Y) * a);
	}
}