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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import pl.reticular.ttw.utils.DBHelper;
import pl.reticular.ttw.utils.ResultsTableHelper;

public class HighScoresActivity extends AppCompatActivity {

	private CursorAdapter adapter;

	private DBHelper dbHelper;

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

		ListView listView = (ListView) findViewById(R.id.list_high_scores);
		TextView noGames = (TextView) findViewById(R.id.text_no_games);

		listView.setEmptyView(noGames);

		listView.addHeaderView(createHeader(listView));

		adapter = new HighScoresCursorAdapter(this, null, 0);

		listView.setAdapter(adapter);

		dbHelper = new DBHelper(this);
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

		Cursor cursor = createCursor();
		adapter.changeCursor(cursor);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(getClass().getName(), "onStop");

		dbHelper.close();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the clear button
			case R.id.action_clear:
				clearHighScores();
				Cursor cursor = createCursor();
				adapter.changeCursor(cursor);
				break;
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
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

	private Cursor createCursor() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String table = ResultsTableHelper.RESULT.TABLE_NAME;

		String[] columns = new String[]{
				ResultsTableHelper.RESULT._ID,
				ResultsTableHelper.RESULT.COLUMN_DATE,
				ResultsTableHelper.RESULT.COLUMN_SCORE
		};

		String orderBy = ResultsTableHelper.RESULT.COLUMN_SCORE + " DESC, " +
				ResultsTableHelper.RESULT.COLUMN_DATE + " DESC";

		return db.query(table, columns, null, null, null, null, orderBy, null);
	}

	private void clearHighScores() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(ResultsTableHelper.RESULT.TABLE_NAME, null, null);
	}

	private class HighScoresCursorAdapter extends CursorAdapter {
		public HighScoresCursorAdapter(Context context, Cursor cursor, int flags) {
			super(context, cursor, flags);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.layout_high_score, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView dateText = (TextView) view.findViewById(R.id.text_date);
			TextView scoreText = (TextView) view.findViewById(R.id.text_score);

			long date = cursor.getLong(cursor.getColumnIndexOrThrow(ResultsTableHelper.RESULT.COLUMN_DATE));
			int score = cursor.getInt(cursor.getColumnIndexOrThrow(ResultsTableHelper.RESULT.COLUMN_SCORE));

			long now = System.currentTimeMillis();
			String timeSpan = (String) DateUtils.getRelativeTimeSpanString(date, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
			dateText.setText(timeSpan);

			scoreText.setText(String.format("%d", score));
		}
	}
}
