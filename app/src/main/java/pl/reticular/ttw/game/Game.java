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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

import pl.reticular.ttw.utils.Savable;
import pl.reticular.ttw.utils.Vector2;

public class Game implements Savable {
	private Context context;

	private enum Keys {
		Web,
		Finger,
		Spiders,
		LivesLeft,
		Level,
		Score
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

	private Bitmap backgroundImage;

	private Finger finger;

	private Web web;

	private Vector2 moveStart;
	private Particle movedParticle;

	private int livesLeft;
	private int level;
	private int score;

	private LinkedList<Spider> spiders;

	private Vector2 gravity;

	public Game(Context context, Handler messageHandler) {
		this.context = context;
		this.messageHandler = messageHandler;

		//Resources res = context.getResources();
		//backgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);

		livesLeft = 1;
		level = 1;
		score = 0;

		createWeb();

		finger = new Finger();

		spiders = new LinkedList<>();
		for (int i = 0; i < level; i++) {
			spiders.add(new Spider(web));
		}

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

		web = new Web(json.getJSONObject(Keys.Web.toString()));
		setupWebObserver();

		finger = new Finger(json.getJSONObject(Keys.Finger.toString()));

		spiders = new LinkedList<>();
		JSONArray spidersData = json.getJSONArray(Keys.Spiders.toString());
		for (int i = 0; i < spidersData.length(); i++) {
			JSONObject spiderState = spidersData.getJSONObject(i);
			Spider spider = new Spider(web, spiderState);
			spiders.add(spider);
		}

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();

		gravity = new Vector2(0.0f, 1.0f);
	}

	@Override
	public JSONObject toJSON() throws JSONException {

		JSONObject state = new JSONObject();

		state.put(Keys.Web.toString(), web.toJSON());
		state.put(Keys.Finger.toString(), finger.toJSON());

		JSONArray spidersData = new JSONArray();

		for (Spider spider : spiders) {
			JSONObject spiderState = spider.toJSON();
			spidersData.put(spiderState);
		}

		state.put(Keys.Spiders.toString(), spidersData);

		state.put(Keys.LivesLeft.toString(), livesLeft);
		state.put(Keys.Level.toString(), level);
		state.put(Keys.Score.toString(), score);

		return state;
	}

	private void createWeb() {
		web = new RoundWeb(0, 0, 0.1f, 0.9f, 3, 8, 0.1f);
		setupWebObserver();
	}

	private void setupWebObserver() {
		web.setObserver(new WebObserver() {
			@Override
			public void onSpringBroken(Spring spring) {
				if (!isFinished()) {
					setParticleToMove(null);
					addScore(1);
				}

				for (Spider spider : spiders) {
					spider.onSpringUnAvailable(spring);
				}
			}

			@Override
			public void onSpringOut(Spring spring) {
				for (Spider spider : spiders) {
					spider.onSpringUnAvailable(spring);
				}
			}
		});
	}

	private void levelUp() {
		level += 1;
		livesLeft = level;

		createWeb();

		finger = new Finger();

		spiders = new LinkedList<>();
		for (int i = 0; i < level; i++) {
			spiders.add(new Spider(web));
		}

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();
		messageLevelUp();
	}

	public void messageSpidersLeft() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.SpidersLeft.toString());
		b.putInt(MessageFields.Data.toString(), spiders.size());
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageLivesLeft() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.LivesLeft.toString());
		b.putInt(MessageFields.Data.toString(), livesLeft);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageScore() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.Score.toString());
		b.putInt(MessageFields.Data.toString(), score);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageLevelUp() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.LevelUp.toString());
		b.putInt(MessageFields.Data.toString(), level);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageGameOver() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(MessageFields.Type.toString(), MessageType.GameOver.toString());
		b.putInt(MessageFields.Data.toString(), score);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public boolean isFinished() {
		return livesLeft <= 0;
	}

	public void setSurfaceSize(int width, int height) {
		canvasWidth = width;
		canvasHeight = height;

		// resize the background image
		//backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);

		int min = Math.min(width, height);
		canvasScale = min * 0.5f;

		float x = (float) width / (float) min;
		float y = (float) height / (float) min;
		gameArea = new RectF(-x, -y, x, y);
	}

	public void frame(Canvas canvas, float dt) {
		update(dt);
		draw(canvas);
	}

	public void draw(Canvas canvas) {
		canvas.save();

		canvas.drawColor(Color.BLACK);
		//canvas.drawBitmap(backgroundImage, 0, 0, null);

		canvas.translate(canvasWidth / 2, canvasHeight / 2);

		web.draw(canvas, canvasScale);

		for (Spider spider : spiders) {
			spider.draw(canvas, canvasScale);
		}

		finger.draw(canvas, canvasScale);

		canvas.restore();
	}

	private void addScore(int add) {
		score += add * level;
		messageScore();
	}

	public int getScore() {
		return score;
	}

	public void update(float dt) {

		web.update(dt, gravity, gameArea);

		Iterator<Spider> it = spiders.iterator();
		while (it.hasNext()) {
			Spider spider = it.next();
			try {
				spider.update(dt, gravity, gameArea);
			} catch (Spider.OutException e) {
				if (!isFinished()) {
					it.remove();
					messageSpidersLeft();
					addScore(10);
				}
			}
		}

		if (spiders.size() == 0) {
			levelUp();
		}

		finger.update(dt);

		//check game over conditions
		if (!isFinished()) {
			if (finger.isFreshlyPoisoned(spiders)) {
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

	void moveParticle(Vector2 move) {
		if (movedParticle != null) {
			Vector2 oldPos = new Vector2(movedParticle.getPos());
			oldPos.add(move);
			movedParticle.setPinnedPos(oldPos);
		}
	}

	public void onTouchEvent(MotionEvent motionEvent) {
		/*
		web.destroyNearestSpring(motionEvent.getX() - canvasWidth / 2, motionEvent.getY() - canvasHeight / 2, 3.0f);
		*/
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Vector2 click = new Vector2(
						motionEvent.getX() - canvasWidth / 2,
						motionEvent.getY() - canvasHeight / 2);
				click.scale(1.0f / canvasScale);

				Particle pulled = web.selectParticleInRange(click, finger.getRadius());
				if (pulled != null) {
					for (Spider spider : spiders) {
						spider.onParticlePulled(pulled);
					}
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

	public void setGravity(float x, float y, float z) {
		gravity.set(-x, y + Math.abs(z)); // y is backwards, so here is positive
		gravity.scale(0.2f);
	}
}
