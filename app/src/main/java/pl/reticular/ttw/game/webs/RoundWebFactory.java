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

public class RoundWebFactory {
	public static Web createRoundWeb(float xs, float ys, float rMin, float rMax, int cylinders, int sectors, float spacing) {
		Web web = new Web();

		float baseAngle = (float) Math.PI * (-0.5f + 1.0f / sectors);

		for (int c = 0; c < cylinders; c++) {

			for (int s = 0; s < sectors; s++) {
				boolean pinned = (c == cylinders - 1);

				float angle = baseAngle + s * ((float) Math.PI * 2.0f / sectors);
				float r = rMin + c * ((rMax - rMin) / (cylinders - 1));

				Particle p = new Particle(xs + r * (float) Math.cos(angle),
						ys + r * (float) Math.sin(angle),
						pinned);
				web.insert(p);
			}
		}

		for (int c = 0; c < cylinders; c++) {

			for (int s = 0; s < sectors; s++) {
				if (s != 0) {
					web.addNormalizedChain((Particle) web.getNode(c * sectors + s),
							(Particle) web.getNode(c * sectors + s - 1),
							spacing);
				}

				if (c != 0) {
					web.addNormalizedChain((Particle) web.getNode(c * sectors + s),
							(Particle) web.getNode((c - 1) * sectors + s),
							spacing);
				}
			}
			web.addNormalizedChain((Particle) web.getNode((c + 1) * sectors - 1),
					(Particle) web.getNode(c * sectors),
					spacing);
		}

		return web;
	}

	public static void generateBackground(Canvas canvas, Bitmap backgroundBitmap, int backgroundColor, int areaColor, int borderColor,
	                                      float scale, float xs, float ys, float rMax) {

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

		canvas.drawCircle(xs * scale, ys * scale, rMax * scale, areaPaint);

		canvas.drawCircle(xs * scale, ys * scale, rMax * scale, borderPaint);
	}
}
