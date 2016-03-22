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

import android.graphics.Canvas;
import android.graphics.RectF;

public class CanvasHelper {

	private int height;
	private int width;
	private float scale;

	private RectF area;

	public CanvasHelper() {
		width = height = 1;
		scale = 1.0f;
		area = new RectF(-1.0f, -1.0f, 1.0f, 1.0f);
	}

	public void setSize(int w, int h) {
		width = w;
		height = h;

		// calculate scale and game area
		int min = Math.min(width, height);
		scale = min * 0.5f;

		float x = (float) width / (float) min;
		float y = (float) height / (float) min;
		area = new RectF(-x, -y, x, y);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getScale() {
		return scale;
	}

	public RectF getArea() {
		return area;
	}

	public Vector2 transform(float x, float y) {
		Vector2 ret = new Vector2(x - width / 2, y - height / 2);
		ret.scale(1.0f / scale);
		return ret;
	}

	public void translate(Canvas canvas) {
		canvas.translate(width / 2, height / 2);
	}
}
