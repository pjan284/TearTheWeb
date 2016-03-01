package pl.reticular.ttw.game;

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

public class SquareWeb extends Web {

	SquareWeb(float x1, float y1, float x2, float y2, int width, int height, int segments) {
		super();

		float xs = (x2 - x1) / (width * segments);
		float ys = (y2 - y1) / (height * segments);

		for (int y = 0; y <= height; y++) {

			for (int x = 0; x <= width; x++) {
				boolean pinned = false;

				if (y == 0) {
					pinned = true;
				}

				Particle p = new Particle(x1 + x * xs, y1 + y * ys, pinned);
				insert(p);

				if (x != 0) {
					Spring spring = new Spring(this, p, (Particle) nodes.get(nodes.size() - 2));
					insert(spring);
				}

				if (y != 0) {
					Spring spring = new Spring(this, p, (Particle) nodes.get(x + (y - 1) * (width + 1)));
					insert(spring);
				}
			}
		}
		//print();
	}
}
