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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import pl.reticular.ttw.game.graph.Edge;
import pl.reticular.ttw.game.graph.GraphObserver;
import pl.reticular.ttw.utils.Vector2;

public class Game {
	private Context context;

	private static final String KEY_WEB = "Web";
	private static final String KEY_FINGER = "Finger";
	private static final String KEY_SPIDERS = "Spiders";
	private static final String KEY_SPIDERS_NUM = "SpidersNum";
	private static final String KEY_LIVES_LEFT = "LivesLeft";
	private static final String KEY_LEVEL = "Level";
	private static final String KEY_SCORE = "Score";

	public static final String MESSAGE_TYPE = "MessageType";
	public static final String MESSAGE_DATA = "Data";

	public static final int MESSAGE_LIVES_LEFT = 1;
	public static final int MESSAGE_SPIDERS_LEFT = 2;
	public static final int MESSAGE_SCORE = 3;
	public static final int MESSAGE_LEVEL_UP = 4;
	public static final int MESSAGE_GAME_OVER = 5;

	private Handler messageHandler;

	public class GameFinishedException extends Exception {
	}

	private int canvasHeight = 1;
	private int canvasWidth = 1;
	private float canvasScale = 1.0f;

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

	public Game(Context context, Handler messageHandler, JSONObject json) throws JSONException, GameFinishedException {
		this.context = context;
		this.messageHandler = messageHandler;

		livesLeft = json.getInt(KEY_LIVES_LEFT);

		if (isFinished()) {
			throw new GameFinishedException();
		}

		level = json.getInt(KEY_LEVEL);
		score = json.getInt(KEY_SCORE);

		web = new Web(json.getJSONObject(KEY_WEB));
		setupWebObserver();

		finger = new Finger(json.getJSONObject(KEY_FINGER));

		spiders = new LinkedList<>();
		int numSpiders = json.getInt(KEY_SPIDERS_NUM);
		for (int i = 0; i < numSpiders; i++) {
			spiders.add(new Spider(web));
		}

		messageLivesLeft();
		messageSpidersLeft();
		messageScore();

		gravity = new Vector2(0.0f, 1.0f);
	}

	public JSONObject toJSON() throws JSONException, GameFinishedException {
		if (isFinished()) {
			throw new GameFinishedException();
		}

		JSONObject state = new JSONObject();

		state.put(KEY_WEB, web.toJSON());
		state.put(KEY_FINGER, finger.toJSON());
		state.put(KEY_SPIDERS_NUM, spiders.size());
		state.put(KEY_LIVES_LEFT, livesLeft);
		state.put(KEY_LEVEL, level);
		state.put(KEY_SCORE, score);

		return state;
	}

	private void createWeb() {
		web = new RoundWeb(0, 0, 0.1f, 0.9f, 3, 8, 0.1f);
		setupWebObserver();
	}

	private void setupWebObserver() {
		web.setObserver(new GraphObserver() {
			@Override
			public void onEdgeRemoved(Edge edge) {
				if (!isFinished()) {
					setParticleToMove(null);
					addScore(1);
				}

				for (Spider spider : spiders) {
					spider.onEdgeRemoved(edge);
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
		b.putInt(MESSAGE_TYPE, MESSAGE_SPIDERS_LEFT);
		b.putInt(MESSAGE_DATA, spiders.size());
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageLivesLeft() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_TYPE, MESSAGE_LIVES_LEFT);
		b.putInt(MESSAGE_DATA, livesLeft);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageScore() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_TYPE, MESSAGE_SCORE);
		b.putInt(MESSAGE_DATA, score);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageLevelUp() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_TYPE, MESSAGE_LEVEL_UP);
		b.putInt(MESSAGE_DATA, level);
		msg.setData(b);
		messageHandler.sendMessage(msg);
	}

	public void messageGameOver() {
		Message msg = messageHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(MESSAGE_TYPE, MESSAGE_GAME_OVER);
		b.putInt(MESSAGE_DATA, score);
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
		canvasScale = min / 2;
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

		web.update(dt, gravity);

		LinkedList<Spider> spidersToRemove = new LinkedList<>();

		for (Spider spider : spiders) {
			spider.update(dt, gravity);

			// check level up conditions
			if (!isFinished()) {
				Vector2 pos = spider.getPosition();
				float x = pos.X * canvasScale + canvasWidth / 2;
				float y = pos.Y * canvasScale + canvasHeight / 2;
				if (x < 0 || x > canvasWidth || y < 0 || y > canvasHeight) {
					spidersToRemove.add(spider);
				}
			}
		}

		for (Spider spider : spidersToRemove) {
			spiders.remove(spider);
			messageSpidersLeft();
			addScore(10);
			if (spiders.size() == 0) {
				levelUp();
			}
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
