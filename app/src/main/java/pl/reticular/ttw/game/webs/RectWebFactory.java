package pl.reticular.ttw.game.webs;

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

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import pl.reticular.ttw.game.Particle;
import pl.reticular.ttw.game.Web;

public class RectWebFactory {
	public static Web createRectWeb(float left, float top, float right, float bottom, int xGaps, int yGaps, float spacing) {
		Web web = new Web();

		float width = right - left;
		float height = bottom - top;

		float xs = width / xGaps;
		float ys = height / yGaps;

		Particle previousKeyParticles[] = null;

		for (int y = 0; y <= yGaps; y++) {

			Particle keyParticles[] = new Particle[xGaps + 1];

			for (int x = 0; x <= xGaps; x++) {
				boolean pinned = (x == 0 || y == 0 || x == xGaps || y == xGaps);

				Particle p = new Particle(left + (x * xs), top + (y * ys), pinned);
				web.insert(p);
				keyParticles[x] = p;

				// horizontal chain
				if (x > 0 && y != 0 && y != yGaps) {
					web.addNormalizedChain(p, keyParticles[x - 1], spacing);
				}

				// vertical chain
				if (y > 0 && x != 0 && x != xGaps) {
					web.addNormalizedChain(p, previousKeyParticles[x], spacing);
				}
			}

			previousKeyParticles = keyParticles;
		}

		return web;
	}

	public static void generateBackground(Canvas canvas, Bitmap backgroundBitmap, int backgroundColor, int areaColor, int borderColor,
	                                      float scale, float left, float top, float right, float bottom) {

		canvas.drawColor(backgroundColor);

		Paint backgroundPaint = new Paint();
		backgroundPaint.setStyle(Paint.Style.FILL);
		if (backgroundBitmap != null) {
			Shader shader = new BitmapShader(backgroundBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			backgroundPaint.setShader(shader);
		} else {
			backgroundPaint.setColor(Color.TRANSPARENT);
		}

		Paint borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(3.0f);
		borderPaint.setAntiAlias(true);

		Paint areaPaint = new Paint();
		areaPaint.setColor(areaColor);
		areaPaint.setStyle(Paint.Style.FILL);

		canvas.drawRect(canvas.getClipBounds(), backgroundPaint);

		canvas.drawRect(left * scale, top * scale, right * scale, bottom * scale, areaPaint);

		canvas.drawRect(left * scale, top * scale, right * scale, bottom * scale, borderPaint);
	}
}
