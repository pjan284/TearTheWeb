package pl.reticular.ttw.game.model.webs;

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
import android.graphics.Canvas;

import pl.reticular.ttw.game.model.Web;

public class WebFactory {
	public static Web createWeb(WebType webType) {
		Web web = null;

		switch (webType) {
			case Round5x6:
				web = RoundWebFactory.createRoundWeb(0, 0, 0.1f, 0.9f, 5, 6, 0.1f);
				break;
			case Round4x7:
				web = RoundWebFactory.createRoundWeb(0, 0, 0.1f, 0.9f, 4, 7, 0.1f);
				break;
			case Round5x7:
				web = RoundWebFactory.createRoundWeb(0, 0, 0.1f, 0.9f, 5, 7, 0.1f);
				break;
			case Round4x8:
				web = RoundWebFactory.createRoundWeb(0, 0, 0.1f, 0.9f, 4, 8, 0.1f);
				break;
			case Rect5x5:
				web = RectWebFactory.createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 5, 5, 0.1f);
				break;
			case Rect6x6:
				web = RectWebFactory.createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 6, 6, 0.1f);
				break;
			case Rect7x7:
				web = RectWebFactory.createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 7, 7, 0.1f);
				break;
			case Rect8x8:
				web = RectWebFactory.createRectWeb(-0.9f, -0.9f, 0.9f, 0.9f, 8, 8, 0.1f);
				break;
		}

		return web;
	}

	public static void generateBackground(Canvas canvas, Bitmap backgroundBitmap, int backgroundColor, int areaColor, int borderColor,
	                                      float scale, WebType webType) {
		switch (webType) {
			case Round5x6:
				RoundWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, 0, 0, 0.9f);
				break;
			case Round4x7:
				RoundWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, 0, 0, 0.9f);
				break;
			case Round5x7:
				RoundWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, 0, 0, 0.9f);
				break;
			case Round4x8:
				RoundWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, 0, 0, 0.9f);
				break;
			case Rect5x5:
				RectWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, -0.9f, -0.9f, 0.9f, 0.9f);
				break;
			case Rect6x6:
				RectWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, -0.9f, -0.9f, 0.9f, 0.9f);
				break;
			case Rect7x7:
				RectWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, -0.9f, -0.9f, 0.9f, 0.9f);
				break;
			case Rect8x8:
				RectWebFactory.generateBackground(canvas, backgroundBitmap, backgroundColor, areaColor, borderColor, scale, -0.9f, -0.9f, 0.9f, 0.9f);
				break;
		}
	}
}
