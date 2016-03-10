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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import pl.reticular.ttw.utils.Settings;

public class StartActivity extends AppCompatActivity {

	private Button continueButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getClass().getName(), "onCreate");

		setContentView(R.layout.layout_start);

		Button newGameButton = (Button) findViewById(R.id.button_new_game);
		continueButton = (Button) findViewById(R.id.button_continue);
		Button highScoresButton = (Button) findViewById(R.id.button_high_scores);
		ImageButton moreButton = (ImageButton) findViewById(R.id.button_start_more);

		newGameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onNewGame();
			}
		});

		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onContinue();
			}
		});

		highScoresButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onHighScores();
			}
		});

		moreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMoreMenu(v);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(getClass().getName(), "onResume");

		continueButton.setEnabled(Settings.getInstance().hasLastGame(this));
	}

	private void showMoreMenu(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_about:
						onAbout();
						return true;
					case R.id.action_help:
						onHelp();
						return true;
					default:
						return false;
				}
			}
		});
		popup.inflate(R.menu.menu_start_more);
		popup.show();

	}

	private void onAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private void onContinue() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		intent.putExtra(GameActivity.KEY_CONTINUE_GAME, true);

		startActivity(intent);
	}

	private void onHelp() {
		Intent intent = new Intent(this, HelpActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private void onHighScores() {
		Intent intent = new Intent(this, HighScoresActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	private void onNewGame() {
		Intent intent = new Intent(this, GameActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
