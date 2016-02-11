package pl.reticular.ttw.utils;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModuloTest {

	@Test
	public void toRange_isCorrect() throws Exception {
		float smallNum = 0.0001f;

		assertEquals(0.0f, Modulo.toRange(0.0f, 8.0f), smallNum);

		assertEquals(0.0f, Modulo.toRange(8.0f, 8.0f), smallNum);

		assertEquals(1.0f, Modulo.toRange(9.0f, 8.0f), smallNum);

		assertEquals(7.0f, Modulo.toRange(-1.0f, 8.0f), smallNum);

		assertEquals(0.0f, Modulo.toRange(-8.0f, 8.0f), smallNum);

		assertEquals(7.0f, Modulo.toRange(-9.0f, 8.0f), smallNum);
	}

	@Test
	public void shortestDistance_isCorrect() throws Exception {
		float smallNum = 0.0001f;

		assertEquals(0.0f, Modulo.shortestDistance(0.0f, 0.0f, 1.0f), smallNum);

		assertEquals(0.0f, Modulo.shortestDistance(0.0f, 0.0f, 8.0f), smallNum);

		assertEquals(1.0f, Modulo.shortestDistance(1.0f, 2.0f, 8.0f), smallNum);

		assertEquals(1.0f, Modulo.shortestDistance(2.0f, 1.0f, 8.0f), smallNum);

		assertEquals(1.0f, Modulo.shortestDistance(7.0f, 0.0f, 8.0f), smallNum);

		assertEquals(1.0f, Modulo.shortestDistance(0.0f, 7.0f, 8.0f), smallNum);
	}

	@Test
	public void shortestDistanceDirection_isCorrect() throws Exception {
		assertEquals(false, Modulo.shortestDistanceDirection(0.0f, 0.0f, 8.0f));

		assertEquals(true, Modulo.shortestDistanceDirection(0.0f, 1.0f, 8.0f));

		assertEquals(false, Modulo.shortestDistanceDirection(1.0f, 7.0f, 8.0f));

		assertEquals(false, Modulo.shortestDistanceDirection(1.0f, 0.0f, 8.0f));

		assertEquals(true, Modulo.shortestDistanceDirection(7.0f, 1.0f, 8.0f));
	}

	@Test
	public void angleDifference_isCorrect() throws Exception {
		float smallNum = 0.0001f;

		assertEquals(0.0f, Modulo.angleDifference(0.0f, 0.0f), smallNum);

		assertEquals(0.0f, Modulo.angleDifference(360.0f, 0.0f), smallNum);

		assertEquals(0.0f, Modulo.angleDifference(0.0f, 360.0f), smallNum);

		assertEquals(0.0f, Modulo.angleDifference(360.0f, 360.0f), smallNum);

		assertEquals(0.0f, Modulo.angleDifference(0.0f, 720.0f), smallNum);

		assertEquals(0.0f, Modulo.angleDifference(720.0f, 0.0f), smallNum);

		assertEquals(90.0f, Modulo.angleDifference(0.0f, 90.0f), smallNum);

		assertEquals(-90.0f, Modulo.angleDifference(90.0f, 0.0f), smallNum);

		assertEquals(90.0f, Modulo.angleDifference(-90.0f, 0.0f), smallNum);

		assertEquals(-90.0f, Modulo.angleDifference(0.0f, -90.0f), smallNum);

		assertEquals(-90.0f, Modulo.angleDifference(0.0f, 270.0f), smallNum);

		assertEquals(90.0f, Modulo.angleDifference(270.0f, 0.0f), smallNum);
	}
}
