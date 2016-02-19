package pl.reticular.ttw;

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

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.lang.ref.WeakReference;

import pl.reticular.ttw.game.Game;
import pl.reticular.ttw.game.display.GameSurfaceView;
import pl.reticular.ttw.utils.Settings;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

	private GameSurfaceView gameSurfaceView;
	private TextView topLeftText;
	private TextView topRightText;

	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private boolean sensorAvailable;

	public static final String KEY_CONTINUE_GAME = "ContinueGame";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(getClass().getName(), "onCreate");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorAvailable = false;    //for now

		setContentView(R.layout.layout_game);

		gameSurfaceView = (GameSurfaceView) findViewById(R.id.view_game);
		topLeftText = (TextView) findViewById(R.id.text_top_left);
		topRightText = (TextView) findViewById(R.id.text_top_right);

		Bundle bundle = getIntent().getExtras();
		Boolean continueGame = false;
		if (bundle != null) {
			continueGame = bundle.getBoolean(KEY_CONTINUE_GAME, false);
		}

		// try to load last played game
		Game lastGame = null;
		if (Settings.hasLastGame(this)) {
			try {
				lastGame = new Game(this, new MessageHandler(this), Settings.getLastGame(this));
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Game.GameFinishedException e) {
				Settings.clearLastGame(this);
			}
		}

		Game game;
		if (continueGame) {
			if (lastGame != null) {
				game = lastGame;
			} else {
				game = new Game(this, new MessageHandler(this));
			}
		} else {
			if (lastGame != null) {
				Settings.saveHighScore(this, lastGame.getScore());
			}
			Settings.clearLastGame(this);
			game = new Game(this, new MessageHandler(this));
		}

		gameSurfaceView.setGame(game);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(getClass().getName(), "onResume");

		if (accelerometerSensor != null) {
			sensorAvailable = sensorManager.registerListener(this,
					accelerometerSensor,
					SensorManager.SENSOR_DELAY_UI);
		}

		gameSurfaceView.getThread().resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(getClass().getName(), "onPause");

		if (sensorAvailable) {
			sensorManager.unregisterListener(this);
			sensorAvailable = false;
		}

		gameSurfaceView.getThread().pause();

		if (!gameSurfaceView.getGame().isFinished()) {
			try {
				Settings.saveLastGame(this, gameSurfaceView.getGame().toJSON());
			} catch (JSONException e) {
				e.printStackTrace();
				Settings.clearLastGame(this);
			} catch (Game.GameFinishedException e) {
				Settings.clearLastGame(this);
			}
		} else {
			Settings.clearLastGame(this);
		}
	}

	protected void onStop() {
		super.onStop();
		Log.i(getClass().getName(), "onStop");

		finish();
	}

	private static class MessageHandler extends Handler {
		//Using a weak reference means we won't prevent garbage collection
		private final WeakReference<GameActivity> gameActivityWeakReference;

		public MessageHandler(GameActivity instance) {
			gameActivityWeakReference = new WeakReference<>(instance);
		}

		@Override
		public void handleMessage(Message msg) {
			GameActivity gameActivity = gameActivityWeakReference.get();
			if (gameActivity != null) {
				switch (msg.getData().getInt(Game.MESSAGE_TYPE)) {
					case Game.MESSAGE_LIVES_LEFT:
						int livesLeft = msg.getData().getInt(Game.MESSAGE_DATA);
						gameActivity.displayLivesLeft(livesLeft);
						break;
					case Game.MESSAGE_SPIDERS_LEFT:
						break;
					case Game.MESSAGE_SCORE:
						int score = msg.getData().getInt(Game.MESSAGE_DATA);
						gameActivity.displayScore(score);
						break;
					case Game.MESSAGE_GAME_OVER:
						int gameScore = msg.getData().getInt(Game.MESSAGE_DATA);
						gameActivity.gameOver(gameScore);
						break;
					case Game.MESSAGE_LEVEL_UP:
						int level = msg.getData().getInt(Game.MESSAGE_DATA);
						gameActivity.displayLevelUp(level);
						break;
				}
			}
		}
	}

	private void displayLivesLeft(int livesLeft) {
		String string = getResources().getString(R.string.game_lives);
		topRightText.setText(String.format(string, livesLeft));
	}

	private void displayScore(int score) {
		String string = getResources().getString(R.string.game_score);
		topLeftText.setText(String.format(string, score));
	}

	private void gameOver(int score) {
		Toast toast = Toast.makeText(this, R.string.game_game_over, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		Settings.clearLastGame(this);
		Settings.saveHighScore(this, score);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				launchHighScores();
			}
		}, 2000);
	}

	private void displayLevelUp(int level) {
		String string = getResources().getString(R.string.game_level);

		Toast toast = Toast.makeText(this, String.format(string, level), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void launchHighScores() {
		Intent intent = new Intent(this, HighScoresActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		float length = (float) Math.sqrt(x * x + y * y + z * z);
		if (length != 0.0f) {
			x /= length;
			y /= length;
			z /= length;
		} else {
			x = 0.0f;
			y = 1.0f;
			z = 0.0f;
		}

		gameSurfaceView.getThread().getGame().setGravity(x, y, z);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//TODO
	}
}
