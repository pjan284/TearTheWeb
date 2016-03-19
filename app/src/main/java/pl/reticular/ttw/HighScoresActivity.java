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

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.reticular.ttw.utils.PrefsHelper;
import pl.reticular.ttw.utils.PrefsListHelper;
import pl.reticular.ttw.game.Result;
import pl.reticular.ttw.utils.Settings;

public class HighScoresActivity extends AppCompatActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private enum Columns {
		Date,
		Score
	}

	private SharedPreferences preferences;

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getClass().getName(), "onCreate");

		setContentView(R.layout.layout_high_scores);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_high_scores);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		listView = (ListView) findViewById(R.id.list_high_scores);
		TextView noGames = (TextView) findViewById(R.id.text_no_games);

		listView.setEmptyView(noGames);

		listView.addHeaderView(createHeader(listView));

		preferences = getSharedPreferences(Settings.SETTINGS_NAME, 0);

		listView.setAdapter(createAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_high_scores, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(getClass().getName(), "onStart");

		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(getClass().getName(), "onStop");

		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the clear button
			case R.id.action_clear:
				PrefsHelper.remove(preferences, Settings.Keys.HighScores.toString());
				// we will be notified when it's done
				break;
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Settings.Keys.valueOf(key) == Settings.Keys.HighScores) {
			listView.setAdapter(createAdapter());
		}
	}

	private Map<String, String> createRowData(Long date, Integer score) {
		Map<String, String> map = new HashMap<>();
		long now = System.currentTimeMillis();
		String timeSpan = (String) DateUtils.getRelativeTimeSpanString(date, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
		map.put(Columns.Date.toString(), timeSpan);
		map.put(Columns.Score.toString(), String.format("%d", score));
		return map;
	}

	private List<Map<String, String>> createData() {
		PrefsListHelper<Result> helper = new PrefsListHelper<>(new Result.ResultFactory());
		List<Result> highScores = helper.getList(preferences, Settings.Keys.HighScores.toString());

		Collections.sort(highScores);

		List<Map<String, String>> list = new LinkedList<>();
		for (Result result : highScores) {
			list.add(createRowData(result.getDate(), result.getScore()));
		}
		return list;
	}

	private ListAdapter createAdapter() {
		List<Map<String, String>> adapterData = createData();

		String[] fromColumns = {Columns.Date.toString(), Columns.Score.toString()};
		int[] toViews = {R.id.text_date, R.id.text_score};

		return new SimpleAdapter(this, adapterData, R.layout.layout_high_score, fromColumns, toViews);
	}

	private View createHeader(ViewGroup listView) {
		View header = getLayoutInflater().inflate(R.layout.layout_high_score, listView, false);
		TextView dateText = (TextView) header.findViewById(R.id.text_date);
		TextView scoreText = (TextView) header.findViewById(R.id.text_score);

		dateText.setTextColor(Color.WHITE);
		scoreText.setTextColor(Color.WHITE);

		dateText.setText(R.string.high_scores_date);
		scoreText.setText(R.string.high_scores_score);

		return header;
	}
}
