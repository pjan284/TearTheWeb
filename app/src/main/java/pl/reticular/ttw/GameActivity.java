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
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import pl.reticular.ttw.game.engine.GameEngine;
import pl.reticular.ttw.game.engine.GameSurfaceView;
import pl.reticular.ttw.game.model.Game;
import pl.reticular.ttw.game.model.meta.MetaData;
import pl.reticular.ttw.game.model.meta.MetaDataMsg;
import pl.reticular.ttw.utils.DBHelper;
import pl.reticular.ttw.utils.Prefs;
import pl.reticular.ttw.utils.PrefsHelper;
import pl.reticular.ttw.utils.ResultsTableHelper;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

	private TextView topLeftText;
	private TextView topRightText;

	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private boolean sensorAvailable;

	public static final String KEY_CONTINUE_GAME = "ContinueGame";

	private SharedPreferences preferences;

	private GameEngine gameEngine;

	private Handler handler;

	private Runnable highScoreLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Log.i(getClass().getName(), "onCreate");

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorAvailable = false;    //for now

		setContentView(R.layout.layout_game);

		GameSurfaceView gameSurfaceView = (GameSurfaceView) findViewById(R.id.view_game);
		topLeftText = (TextView) findViewById(R.id.text_top_left);
		topRightText = (TextView) findViewById(R.id.text_top_right);

		preferences = PrefsHelper.getPrefs(this);

		// load last played game
		Game lastGame = loadLastGame();

		// determine if last game shall be continued
		boolean continueGame = false;
		if (savedInstanceState != null) {
			continueGame = (lastGame != null) && savedInstanceState.getBoolean(KEY_CONTINUE_GAME, false);
		} else {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				continueGame = (lastGame != null) && bundle.getBoolean(KEY_CONTINUE_GAME, false);
			}
		}

		Game game;
		if (continueGame) {
			// last game will be played
			game = lastGame;
		} else {
			if (lastGame != null) {
				saveResult(lastGame.getMetaData());
			}

			//create new game
			game = new Game();
		}

		gameEngine = new GameEngine(this, new MessageHandler(this), game);

		gameSurfaceView.setGameEngine(gameEngine);

		handler = new Handler();

		highScoreLauncher = new Runnable() {
			@Override
			public void run() {
				launchHighScores();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Log.i(getClass().getName(), "onResume");

		if (accelerometerSensor != null) {
			sensorAvailable = sensorManager.registerListener(this,
					accelerometerSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Log.i(getClass().getName(), "onPause");

		if (sensorAvailable) {
			sensorManager.unregisterListener(this);
			sensorAvailable = false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(KEY_CONTINUE_GAME, true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.i(getClass().getName(), "onStop");

		if (gameEngine.isFinished()) {
			finish();
		} else {
			saveLastGame(gameEngine.getGame());
		}

		handler.removeCallbacks(highScoreLauncher);
	}

	private void saveLastGame(Game game) {
		try {
			String data = game.toJSON().toString();
			PrefsHelper.putString(preferences, Prefs.LastGame.toString(), data);
		} catch (JSONException e) {
			clearLastGameData();
		}
	}

	private void saveResult(MetaData metaData) {
		// clear last game data
		clearLastGameData();

		// but preserve score
		DBHelper dbHelper = new DBHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ResultsTableHelper.insert(db, metaData);
		dbHelper.close();
	}

	private void clearLastGameData() {
		PrefsHelper.remove(preferences, Prefs.LastGame.toString());
	}

	private Game loadLastGame() {
		if (preferences.contains(Prefs.LastGame.toString())) {
			try {
				String data = preferences.getString(Prefs.LastGame.toString(), "{}");
				JSONObject lastGameData = new JSONObject(data);
				return new Game(lastGameData);
			} catch (JSONException e) {
				clearLastGameData();
			}
		}
		return null;
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
			Bundle data = msg.getData();
			if (gameActivity != null) {
				try {
					MetaDataMsg.Reason reason = MetaDataMsg.Reason.valueOf(data.getString(MetaDataMsg.Fields.Reason.toString()));
					JSONObject json = new JSONObject(data.getString(MetaDataMsg.Fields.Data.toString()));
					MetaData metaData = new MetaData(json);
					switch (reason) {
						case Init:
							gameActivity.displayLivesLeft(metaData.getLives());
							gameActivity.displayScore(metaData.getScore());
							break;
						case LivesDecreased:
							gameActivity.displayLivesLeft(metaData.getLives());
							break;
						case ScoreChanged:
							gameActivity.displayScore(metaData.getScore());
							break;
						case GameOver:
							gameActivity.displayLivesLeft(metaData.getLives());
							gameActivity.gameOver(metaData);
							break;
						case LevelUp:
							gameActivity.displayLevelUp(metaData.getLevel());
							break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
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

	private void gameOver(MetaData metaData) {
		Toast toast = Toast.makeText(this, R.string.game_game_over, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		saveResult(metaData);

		handler.postDelayed(highScoreLauncher, 2000);
	}

	private void displayLevelUp(int level) {
		String string = getResources().getString(R.string.game_level);

		Toast toast = Toast.makeText(this, String.format(string, level), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void launchHighScores() {
		Intent intent = new Intent(this, HighScoresActivity.class);
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

		gameEngine.setGravity(x, y, z);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//TODO
	}
}
