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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_about);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_about);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		TextView appVersion = (TextView) findViewById(R.id.app_version);
		int versionCode = BuildConfig.VERSION_CODE;
		String versionName = BuildConfig.VERSION_NAME;
		String version = String.format("v. %s (%d)", versionName, versionCode);
		appVersion.setText(version);

		Button licenseButton = (Button) findViewById(R.id.button_license);
		licenseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLicense();
			}
		});

		Button sourceButton = (Button) findViewById(R.id.button_source);
		sourceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSource();
			}
		});
	}

	private void showLicense() {
		String url = "https://www.gnu.org/licenses/gpl.html";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(getClass().getName(), "cannot open browser");
		}
	}

	private void showSource() {
		String url = "https://github.com/pjan284/TearTheWeb";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(getClass().getName(), "cannot open browser");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
