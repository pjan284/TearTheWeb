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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.util.ArrayList;

public class RotatedImages {
	public static final int SIZE = 32;
	private ArrayList<Bitmap> images;
	private Vector2 baseImageVector;

	public RotatedImages(Context context, int id) {
		Bitmap temp = BitmapFactory.decodeResource(context.getResources(), id);
		Bitmap spiderImage = Bitmap.createScaledBitmap(temp, SIZE, SIZE, true);
		baseImageVector = new Vector2(0.0f, 1.0f);
		images = new ArrayList<>(360);
		for (int i = 0; i < 360; i++) {
			Matrix rotMatrix = new Matrix();
			rotMatrix.preRotate(360 - 1 - i);
			images.add(Bitmap.createBitmap(spiderImage, 0, 0, SIZE, SIZE, rotMatrix, true));
		}
		temp.recycle();
	}

	public Bitmap get(int id) {
		return images.get(id);
	}

	public Vector2 getBaseImageVector() {
		return baseImageVector;
	}
}
