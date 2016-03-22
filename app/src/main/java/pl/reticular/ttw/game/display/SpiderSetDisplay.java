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

import pl.reticular.ttw.game.model.Spider;
import pl.reticular.ttw.game.model.SpiderSet;

public class SpiderSetDisplay {
	private SpiderDisplay spiderDisplay;

	public SpiderSetDisplay() {
		spiderDisplay = new SpiderDisplay();
	}

	public void draw(SpiderSet spiderSet, Canvas canvas, float scale) {
		for (Spider spider : spiderSet.getSpiders()) {
			spiderDisplay.draw(spider, canvas, scale);
		}
	}
}
