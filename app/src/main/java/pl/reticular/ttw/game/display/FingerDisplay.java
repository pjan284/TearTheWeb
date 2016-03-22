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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import pl.reticular.ttw.game.model.Finger;
import pl.reticular.ttw.utils.Vector2;

public class FingerDisplay {

	private Paint healthyPaint;
	private Paint bittenPaint;

	FingerDisplay() {
		healthyPaint = new Paint();
		healthyPaint.setStyle(Paint.Style.STROKE);
		healthyPaint.setStrokeWidth(3.0f);
		healthyPaint.setColor(Color.YELLOW);

		bittenPaint = new Paint();
		bittenPaint.setStyle(Paint.Style.STROKE);
		bittenPaint.setStrokeWidth(3.0f);
		bittenPaint.setColor(Color.RED);
	}

	public void draw(Finger finger, Canvas canvas, float scale) {
		Vector2 position = finger.getPosition();
		if (position != null) {
			Paint paint;
			if (finger.isBitten()) {
				paint = bittenPaint;
			} else {
				paint = healthyPaint;
			}
			canvas.drawCircle(position.X * scale, position.Y * scale, finger.getRadius() * scale, paint);
		}
	}
}
