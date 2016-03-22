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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import pl.reticular.ttw.R;
import pl.reticular.ttw.game.webs.WebFactory;
import pl.reticular.ttw.game.webs.WebType;
import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Game implements Savable, Web.WebObserver, SpiderManager.SpiderObserver {
	private Context context;

	private enum Keys {
		Web,
		WebType,
		Finger,
		SpiderManager,
		LivesLeft,
		Level,
		Score,
		ScoreDate
	}

	public enum MessageFields {
		Type,
		Data
	}

	public enum MessageType {
		LivesLeft,
		SpidersLeft,
		Score,
		LevelUp,
		GameOver
	}

	private Handler messageHandler;

	private int canvasHeight = 1;
	private int canvasWidth = 1;
	private float canvasScale = 1.0f;

	private RectF gameArea;

	private Bitmap backgroundBitmap;

	private Finger finger;

	private Web web;

	private WebType webType;

	private Vector2 moveStart;
	private Particle movedParticle;

	private int livesLeft;
	private int level;
	private int score;
	private long scoreDate;

	private SpiderManager spiderManager;

	private Vector2 gravity;

	public Game(Context context, Handler messageHandler, WebType webType) {
		this.context = context;
		this.messageHandler = messageHandler;
		this.webType = webType;

		livesLeft = 1;
		level = 1;
		score = 0;
		scoreDate = System.currentTimeMillis();

		web = WebFactory.createWeb(webType);
		web.setObserver(this);

		finger = new Finger();

		spiderManager = new SpiderManager(this);
		spiderManager.populate(level, web);

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();

		gravity = new Vector2(0.0f, 1.0f);
	}

	public Game(Context context, Handler messageHandler, JSONObject json) throws JSONException {
		this.context = context;
		this.messageHandler = messageHandler;

		livesLeft = json.getInt(Keys.LivesLeft.toString());

		level = json.getInt(Keys.Level.toString());
		score = json.getInt(Keys.Score.toString());
		scoreDate = json.getLong(Keys.ScoreDate.toString());

		web = new Web(json.getJSONObject(Keys.Web.toString()));
		web.setObserver(this);

		try {
			webType = WebType.valueOf(json.getString(Keys.WebType.toString()));
		} catch (IllegalArgumentException e) {
			webType = WebType.Round4x8;
		}

		finger = new Finger(json.getJSONObject(Keys.Finger.toString()));

		spiderManager = new SpiderManager(json.getJSONObject(Keys.SpiderManager.toString()), web, this);

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();

		gravity = new Vector2(0.0f, 1.0f);
	}

	@Override
	public synchronized JSONObject toJSON() throws JSONException {

		JSONObject state = new JSONObject();

		state.put(Keys.Web.toString(), web.toJSON());
		state.put(Keys.WebType.toString(), webType.toString());
		state.put(Keys.Finger.toString(), finger.toJSON());

		state.put(Keys.SpiderManager.toString(), spiderManager.toJSON());

		state.put(Keys.LivesLeft.toString(), livesLeft);
		state.put(Keys.Level.toString(), level);
		state.put(Keys.Score.toString(), score);
		state.put(Keys.ScoreDate.toString(), scoreDate);

		return state;
	}

	public synchronized void setSurfaceSize(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		// calculate scale and game area
		int min = Math.min(width, height);
		canvasScale = min * 0.5f;

		float x = (float) width / (float) min;
		float y = (float) height / (float) min;
		gameArea = new RectF(-x, -y, x, y);

		setupBackground(width, height);
	}

	public synchronized void onTouchEvent(MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Vector2 click = new Vector2(
						motionEvent.getX() - canvasWidth / 2,
						motionEvent.getY() - canvasHeight / 2);
				click.scale(1.0f / canvasScale);

				Particle pulled = web.selectParticleInRange(click, finger.getRadius());
				if (pulled != null) {
					spiderManager.onParticlePulled(pulled);

					finger.setPos(click);
					finger.setVisible(true);
					setParticleToMove(pulled);
				}
				moveStart = click;
				break;
			case MotionEvent.ACTION_MOVE:
				Vector2 move = new Vector2(
						motionEvent.getX() - canvasWidth / 2,
						motionEvent.getY() - canvasHeight / 2);
				move.scale(1.0f / canvasScale);
				moveParticle(Vector2.sub(move, moveStart));
				finger.setPos(move);
				moveStart = move;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				setParticleToMove(null);
				finger.setVisible(false);
		}
	}

	public synchronized void setGravity(float x, float y, float z) {
		gravity.set(-x, y + Math.abs(z)); // y is backwards, so here is positive
		gravity.scale(0.2f);
	}

	public synchronized boolean isFinished() {
		return livesLeft <= 0;
	}

	public synchronized Result getResult() {
		return new Result(scoreDate, level, score, webType);
	}

	private void levelUp() {
		level += 1;
		livesLeft = level;

		webType = WebType.values()[(webType.ordinal() + 1) % WebType.values().length];

		web = WebFactory.createWeb(webType);
		web.setObserver(this);

		backgroundBitmap.recycle();
		setupBackground(canvasWidth, canvasHeight);

		finger = new Finger();

		spiderManager.populate(level, web);

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();
		messageLevelUp();
	}

	private void messageSpidersLeft() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.SpidersLeft.toString());
		b.putInt(MessageFields.Data.toString(), spiderManager.getNumSpiders());
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	private void messageLivesLeft() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.LivesLeft.toString());
		b.putInt(MessageFields.Data.toString(), livesLeft);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	private void messageScore() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.Score.toString());
		b.putInt(MessageFields.Data.toString(), score);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	private void messageLevelUp() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.LevelUp.toString());
		b.putInt(MessageFields.Data.toString(), level);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	private void messageGameOver() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.GameOver.toString());
		b.putInt(MessageFields.Data.toString(), score);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	private void setupBackground(int width, int height) {
		// create background image
		Bitmap backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.web);
		@SuppressWarnings("deprecation")
		int backgroundColor = context.getResources().getColor(R.color.colorBackground);
		this.backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(this.backgroundBitmap);
		backgroundCanvas.translate(width / 2, height / 2);
		WebFactory.generateBackground(backgroundCanvas, backgroundBitmap, backgroundColor, backgroundColor, Color.BLACK, canvasScale, webType);
	}

	public synchronized void frame(Canvas canvas, float dt) {
		update(dt);
		draw(canvas);
	}

	private void draw(Canvas canvas) {
		canvas.save();

		canvas.drawBitmap(backgroundBitmap, 0, 0, null);

		canvas.translate(canvasWidth / 2, canvasHeight / 2);

		web.draw(canvas, canvasScale);

		spiderManager.draw(canvas, canvasScale);

		finger.draw(canvas, canvasScale);

		canvas.restore();
	}

	private void addScore(int add) {
		score += add * level;
		scoreDate = System.currentTimeMillis();
		messageScore();
	}

	private void update(float dt) {

		web.update(dt, gravity, gameArea);

		spiderManager.update(dt, gravity, gameArea);

		if (spiderManager.getNumSpiders() == 0) {
			levelUp();
		}

		finger.update(dt);

		//check game over conditions
		if (!isFinished() && !finger.isPoisoned()) {
			if (spiderManager.areAnyInContactWith(finger)) {
				finger.setPoisoned(true);
				livesLeft--;
				messageLivesLeft();
				if (isFinished()) {
					messageGameOver();
				}
			}

			if (finger.isPoisoned()) {
				setParticleToMove(null);
			}
		}
	}

	private void setParticleToMove(Particle particle) {
		if (particle != null) {
			movedParticle = particle;
			movedParticle.setPinned(true);
		} else {
			if (movedParticle != null) {
				movedParticle.setPinned(false);
				movedParticle = null;
			}
		}
	}

	private void moveParticle(Vector2 move) {
		if (movedParticle != null) {
			Vector2 oldPos = new Vector2(movedParticle.getPos());
			oldPos.add(move);
			movedParticle.setPinnedPos(oldPos);
		}
	}

	@Override
	public void onSpringBroken(Spring spring) {
		if (!isFinished()) {
			setParticleToMove(null);
			addScore(1);
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
			messageSpidersLeft();
			addScore(10);
		}
	}
}
