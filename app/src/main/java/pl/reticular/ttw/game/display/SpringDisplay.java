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

import pl.reticular.ttw.game.model.web.Spring;
import pl.reticular.ttw.utils.Vector2;

public class SpringDisplay {
	private Paint paint;

	public SpringDisplay() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setColor(Color.GRAY);
	}

	public void draw(Spring spring, Canvas canvas, float scale) {
		Vector2 p1 = spring.getParticle1().getPos();
		Vector2 p2 = spring.getParticle2().getPos();

		canvas.drawLine(p1.X * scale, p1.Y * scale, p2.X * scale, p2.Y * scale, paint);
	}
}
