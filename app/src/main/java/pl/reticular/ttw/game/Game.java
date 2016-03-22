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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.R;
import pl.reticular.ttw.game.meta.MetaData;
import pl.reticular.ttw.game.meta.MetaDataHelper;
import pl.reticular.ttw.game.webs.WebFactory;
import pl.reticular.ttw.game.webs.WebType;
import pl.reticular.ttw.utils.CanvasHelper;
import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Game implements Savable, Web.WebObserver, SpiderManager.SpiderObserver {
	private Context context;

	private enum Keys {
		Web,
		Finger,
		SpiderManager,
		Meta
	}

	private CanvasHelper canvasHelper;

	private Bitmap backgroundBitmap;

	private Finger finger;

	private Web web;

	private MetaDataHelper meta;

	private SpiderManager spiderManager;

	private Vector2 gravity;

	public Game(Context context, Handler messageHandler) {
		this.context = context;

		canvasHelper = new CanvasHelper();

		meta = new MetaDataHelper(messageHandler);

		web = WebFactory.createWeb(getWebType(meta.getMetaData().getLevel()));
		web.setObserver(this);

		finger = new Finger();

		spiderManager = new SpiderManager(this);
		spiderManager.populate(meta.getMetaData().getLevel(), web);

		gravity = new Vector2(0.0f, 1.0f);
	}

	public Game(Context context, Handler messageHandler, JSONObject json) throws JSONException {
		this.context = context;

		canvasHelper = new CanvasHelper();

		meta = new MetaDataHelper(new MetaData(json.getJSONObject(Keys.Meta.toString())), messageHandler);

		web = new Web(json.getJSONObject(Keys.Web.toString()));
		web.setObserver(this);

		finger = new Finger(json.getJSONObject(Keys.Finger.toString()));

		spiderManager = new SpiderManager(json.getJSONObject(Keys.SpiderManager.toString()), web, this);

		gravity = new Vector2(0.0f, 1.0f);
	}

	@Override
	public synchronized JSONObject toJSON() throws JSONException {

		JSONObject state = new JSONObject();

		state.put(Keys.Web.toString(), web.toJSON());
		state.put(Keys.Finger.toString(), finger.toJSON());

		state.put(Keys.SpiderManager.toString(), spiderManager.toJSON());

		state.put(Keys.Meta.toString(), meta.getMetaData().toJSON());

		return state;
	}

	public synchronized void setSurfaceSize(int width, int height) {
		canvasHelper.setSize(width, height);

		WebType webType = getWebType(meta.getMetaData().getLevel());
		setupBackground(webType);
	}

	public synchronized void frame(Canvas canvas, float dt) {
		update(dt, gravity, canvasHelper.getArea());
		draw(canvas, canvasHelper.getScale());
	}

	public synchronized void onTouchEvent(MotionEvent motionEvent) {
		Vector2 touch;
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch = canvasHelper.transform(motionEvent.getX(), motionEvent.getY());
				finger.startTracking(touch, web, spiderManager);
				break;
			case MotionEvent.ACTION_MOVE:
				touch = canvasHelper.transform(motionEvent.getX(), motionEvent.getY());
				finger.continueTracking(touch);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				finger.cancelTracking();
				break;
		}
	}

	public synchronized void setGravity(float x, float y, float z) {
		gravity.set(-x, y + Math.abs(z)); // y is backwards, so here is positive
		gravity.scale(0.2f);
	}

	public synchronized boolean isFinished() {
		return meta.getMetaData().isFinished();
	}

	public synchronized MetaData getMetaData() {
		return meta.getMetaData();
	}

	@Override
	public void onSpringBroken(Spring spring) {
		if (!isFinished()) {
			finger.cancelTracking();
			meta.addScore(1);
		}

		spiderManager.onSpringUnAvailable(spring);
	}

	@Override
	public void onSpringOut(Spring spring) {
		spiderManager.onSpringUnAvailable(spring);
	}

	@Override
	public void onSpiderOut(Spider spider) {
		if (!isFinished()) {
			meta.addScore(10);
		}
	}

	private void setupBackground(WebType wt) {
		// read background image
		Bitmap bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.web);

		@SuppressWarnings("deprecation")
		int bgColor = context.getResources().getColor(R.color.colorBackground);

		backgroundBitmap = Bitmap.createBitmap(canvasHelper.getWidth(), canvasHelper.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas bgCanvas = new Canvas(backgroundBitmap);
		canvasHelper.translate(bgCanvas);

		WebFactory.generateBackground(bgCanvas, bgBitmap, bgColor, bgColor, Color.BLACK, canvasHelper.getScale(), wt);
	}

	private void update(float dt, Vector2 gravity, RectF gameArea) {
		web.update(dt, gravity, gameArea);

		spiderManager.update(dt, gravity, gameArea);

		if (spiderManager.getNumSpiders() == 0) {
			levelUp();
		}

		finger.update(dt);

		//check game over conditions
		if (!isFinished() && !finger.isBitten()) {
			if (spiderManager.areAnyInContactWith(finger)) {
				finger.setBitten(true);
				meta.die();
			}
		}
	}

	private void draw(Canvas canvas, float scale) {
		canvas.save();

		canvas.drawBitmap(backgroundBitmap, 0, 0, null);

		canvasHelper.translate(canvas);

		web.draw(canvas, scale);

		spiderManager.draw(canvas, scale);

		finger.draw(canvas, scale);

		canvas.restore();
	}

	private void levelUp() {
		meta.levelUp();

		WebType webType = getWebType(meta.getMetaData().getLevel());
		web = WebFactory.createWeb(webType);
		web.setObserver(this);

		backgroundBitmap.recycle();
		setupBackground(webType);

		spiderManager.populate(meta.getMetaData().getLevel(), web);
	}

	private WebType getWebType(int level) {
		//levels start from 1, enums from 0
		return WebType.values()[(level - 1) % WebType.values().length];
	}
}
