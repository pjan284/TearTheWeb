package pl.reticular.ttw.game.display;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import pl.reticular.ttw.R;
import pl.reticular.ttw.game.model.Game;
import pl.reticular.ttw.game.model.webs.WebFactory;
import pl.reticular.ttw.game.model.webs.WebType;
import pl.reticular.ttw.utils.CanvasHelper;

public class GameDisplay {

	private CanvasHelper canvasHelper;

	private Bitmap backgroundBitmap;

	private WebDisplay webDisplay;
	private SpiderSetDisplay spiderSetDisplay;
	private FingerDisplay fingerDisplay;

	public GameDisplay() {
		canvasHelper = new CanvasHelper();
		backgroundBitmap = null;
		webDisplay = new WebDisplay();
		spiderSetDisplay = new SpiderSetDisplay();
		fingerDisplay = new FingerDisplay();
	}

	public void draw(Game game, Canvas canvas, float scale) {
		canvas.save();

		canvas.drawBitmap(backgroundBitmap, 0, 0, null);

		canvasHelper.translate(canvas);

		webDisplay.draw(game.getWeb(), canvas, scale);

		spiderSetDisplay.draw(game.getSpiderSet(), canvas, scale);

		fingerDisplay.draw(game.getFinger(), canvas, scale);

		canvas.restore();
	}

	public CanvasHelper getCanvasHelper() {
		return canvasHelper;
	}

	public void setupBackground(Context context, WebType wt) {
		// read background image
		Bitmap bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.web);

		@SuppressWarnings("deprecation")
		int bgColor = context.getResources().getColor(R.color.colorBackground);

		if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
			backgroundBitmap.recycle();
		}

		backgroundBitmap = Bitmap.createBitmap(canvasHelper.getWidth(), canvasHelper.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas bgCanvas = new Canvas(backgroundBitmap);
		canvasHelper.translate(bgCanvas);

		WebFactory.generateBackground(bgCanvas, bgBitmap, bgColor, bgColor, Color.BLACK, canvasHelper.getScale(), wt);
	}
}
