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

import pl.reticular.ttw.game.model.Spring;
import pl.reticular.ttw.game.model.Web;
import pl.reticular.ttw.game.model.graph.Edge;

public class WebDisplay {
	private SpringDisplay springDisplay;

	public WebDisplay() {
		springDisplay = new SpringDisplay();
	}

	public void draw(Web web, Canvas canvas, float scale) {
		for (Edge edge : web.getEdges()) {
			springDisplay.draw((Spring) edge, canvas, scale);
		}
	}
}
