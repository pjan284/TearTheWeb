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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import pl.reticular.ttw.R;
import pl.reticular.ttw.game.model.Game;
import pl.reticular.ttw.game.model.web.WebType;
import pl.reticular.ttw.utils.CanvasHelper;

public class GameDisplay {

	private CanvasHelper canvasHelper;

	private Bitmap backgroundBitmap;

	private WebDisplay webDisplay;
	private SpiderSetDisplay spiderSetDisplay;
	private FingerDisplay fingerDisplay;

	private int backgroundColor;
	private Paint backgroundPaint;
	private Paint borderPaint;
	private Paint gameAreaPaint;

	public GameDisplay(Context context) {
		canvasHelper = new CanvasHelper();
		webDisplay = new WebDisplay();
		spiderSetDisplay = new SpiderSetDisplay();
		fingerDisplay = new FingerDisplay();

		backgroundBitmap = null;

		// read app background image
		Bitmap appBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.web);

		backgroundColor = context.getResources().getColor(R.color.colorBackground);
		int borderColor = Color.BLACK;

		backgroundPaint = new Paint();
		backgroundPaint.setStyle(Paint.Style.FILL);
		if (appBackground != null) {
			Shader shader = new BitmapShader(appBackground, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
			backgroundPaint.setShader(shader);
		} else {
			backgroundPaint.setColor(Color.TRANSPARENT);
		}

		borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(3.0f);
		borderPaint.setAntiAlias(true);

		gameAreaPaint = new Paint();
		gameAreaPaint.setColor(backgroundColor);
		gameAreaPaint.setStyle(Paint.Style.FILL);
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

	public void setupBackground(WebType webType) {
		if (backgroundBitmap != null && !backgroundBitmap.isRecycled()) {
			backgroundBitmap.recycle();
		}

		backgroundBitmap = Bitmap.createBitmap(canvasHelper.getWidth(), canvasHelper.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(backgroundBitmap);
		canvasHelper.translate(canvas);

		float scale = canvasHelper.getScale();

		canvas.drawColor(backgroundColor);

		canvas.drawRect(canvas.getClipBounds(), backgroundPaint);

		switch (webType) {
			case Round5x6:
			case Round4x7:
			case Round5x7:
			case Round4x8:
				canvas.drawCircle(0.0f, 0.0f, 0.9f * scale, gameAreaPaint);
				canvas.drawCircle(0.0f, 0.0f, 0.9f * scale, borderPaint);
				break;
			case Rect5x5:
			case Rect6x6:
			case Rect7x7:
			case Rect8x8:
				canvas.drawRect(0.9f * scale, 0.9f * scale, 0.9f * scale, 0.9f * scale, gameAreaPaint);
				canvas.drawRect(0.9f * scale, 0.9f * scale, 0.9f * scale, 0.9f * scale, borderPaint);
				break;
			default:
				break;
		}
	}
}
