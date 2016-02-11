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

public class Modulo {

	/**
	 * @param a any number
	 * @param b any number
	 * @return difference between a and b in range (-360, 360)
	 */
	public static float angleDifference(float a, float b) {
		a = toRange(a, 360.0f);
		b = toRange(b, 360.0f);

		if (shortestDistanceDirection(a, b, 360.0f)) {
			return shortestDistance(a, b, 360.0f);
		} else {
			return -shortestDistance(a, b, 360.0f);
		}
	}

	/**
	 * @param a      any number
	 * @param modulo non-zero positive number
	 * @return number in range [0, modulo)
	 */
	public static float toRange(float a, float modulo) {
		a = a % modulo;

		if (a < 0.0f) {
			a = a + modulo;
		}

		return a;
	}

	/**
	 * @param a      number in range [0, modulo)
	 * @param b      number in range [0, modulo)
	 * @param modulo modulo non-zero positive number
	 * @return true, if by adding small number to a we will get closer to b, false otherwise
	 */
	public static boolean shortestDistanceDirection(float a, float b, float modulo) {
		if (a < b) {
			float diff = b - a;
			return diff < (modulo / 2);
		} else {
			float diff = a - b;
			return diff > (modulo / 2);
		}
	}

	/**
	 * @param a      number in range [0, modulo)
	 * @param b      number in range [0, modulo)
	 * @param modulo non-zero positive number
	 * @return shortest distance in range [0, modulo)
	 */
	public static float shortestDistance(float a, float b, float modulo) {
		if (a < b) {
			float t = a;
			a = b;
			b = t;
		}

		float diff1 = a - b;
		float diff2 = b - a + modulo;
		return Math.min(diff1, diff2);
	}
}
