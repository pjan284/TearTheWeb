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

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.reticular.ttw.utils.Settings;

public class HighScoresActivity extends AppCompatActivity {

	private enum Columns {
		Date,
		Score
	}

	private Map<String, String> createRowData(Long date, Integer score) {
		Map<String, String> map = new HashMap<>();
		long now = System.currentTimeMillis();
		String timeSpan = (String) DateUtils.getRelativeTimeSpanString(date, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
		map.put(Columns.Date.toString(), timeSpan);
		map.put(Columns.Score.toString(), String.format("%d", score));
		return map;
	}

	private List<Map<String, String>> createData(List<Pair<Long, Integer>> highScores) {
		List<Map<String, String>> list = new LinkedList<>();
		for (Pair<Long, Integer> pair : highScores) {
			list.add(createRowData(pair.first, pair.second));
		}
		return list;
	}

	private void setupList() {
		ListView list = (ListView) findViewById(R.id.list_high_scores);
		TextView noGames = (TextView) findViewById(R.id.text_no_games);

		list.setEmptyView(noGames);

		String[] fromColumns = {Columns.Date.toString(), Columns.Score.toString()};
		int[] toViews = {R.id.text_date, R.id.text_score};

		List<Pair<Long, Integer>> highScores = Settings.getHighScores(this);

		Collections.sort(highScores, new Comparator<Pair<Long, Integer>>() {
			@Override
			public int compare(Pair<Long, Integer> lhs, Pair<Long, Integer> rhs) {
				int diff = rhs.second - lhs.second;
				if (diff == 0) {
					long diff2 = rhs.first - lhs.first;
					if (diff2 == 0) {
						return 0;
					} else if (diff2 < 0) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return diff;
				}
			}
		});

		SimpleAdapter adapter = new SimpleAdapter(this, createData(highScores), R.layout.layout_high_score, fromColumns, toViews);

		list.setAdapter(adapter);
	}

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

		setupList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_high_scores, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the clear button
			case R.id.action_clear:
				Settings.clearHighScores(this);
				setupList();
				break;
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
