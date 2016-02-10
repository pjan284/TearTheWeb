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

public class RoundWeb extends Web {

	RoundWeb(float xs, float ys, float rMin, float rMax, int cylinders, int sectors, float spacing) {
		super();

		float baseAngle = (float) Math.PI * 2.0f / (sectors * 2);

		for (int c = 0; c <= cylinders; c++) {

			for (int s = 0; s < sectors; s++) {
				boolean pinned = false;

				if (c == cylinders) {
					pinned = true;
				}

				float angle = baseAngle + s * ((float) Math.PI * 2.0f / sectors);
				float r = rMin + c * ((rMax - rMin) / cylinders);

				Particle p = new Particle(xs + r * (float) Math.cos(angle),
						ys + r * (float) Math.sin(angle),
						pinned);
				insert(p);
			}
		}

		for (int c = 0; c <= cylinders; c++) {

			for (int s = 0; s < sectors; s++) {
				if (s != 0) {
					addNormalizedChain((Particle) nodes.get(c * sectors + s),
							(Particle) nodes.get(c * sectors + s - 1),
							spacing);
				}

				if (c != 0) {
					addNormalizedChain((Particle) nodes.get(c * sectors + s),
							(Particle) nodes.get((c - 1) * sectors + s),
							spacing);
				}
			}
			addNormalizedChain((Particle) nodes.get((c + 1) * sectors - 1),
					(Particle) nodes.get(c * sectors),
					spacing);
		}
	}
}
