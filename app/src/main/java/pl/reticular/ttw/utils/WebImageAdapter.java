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
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import pl.reticular.ttw.R;
import pl.reticular.ttw.game.Web;
import pl.reticular.ttw.game.webs.WebFactory;
import pl.reticular.ttw.game.webs.WebType;

public class WebImageAdapter extends BaseAdapter {

	Context context;

	public WebImageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return WebType.values().length;
	}

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setAdjustViewBounds(true);
		} else {
			imageView = (ImageView) convertView;
		}

		WebType webType = WebType.values()[position];
		Web web = WebFactory.createWeb(webType);

		int width = 256;
		int height = 256;
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);


		canvas.translate(width / 2, height / 2);

		int min = Math.min(width, height);
		float scale = min * 0.5f;

		// background image
		@SuppressWarnings("deprecation")
		int backgroundColor = context.getResources().getColor(R.color.colorBackground);
		WebFactory.generateBackground(canvas, null, Color.TRANSPARENT, backgroundColor, Color.BLACK, scale, webType);

		web.draw(canvas, scale);

		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}


}
