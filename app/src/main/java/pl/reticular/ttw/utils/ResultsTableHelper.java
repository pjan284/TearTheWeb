package pl.reticular.ttw.utils;

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

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import pl.reticular.ttw.game.Result;

public class ResultsTableHelper {
	public static final String TABLE_NAME = "Results";

	public static class RESULT implements BaseColumns {
		public static final String DATE = "date";
		public static final String LEVEL = "level";
		public static final String SCORE = "score";
		public static final String WEB_TYPE = "web_type";
	}

	public static void createTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME + " ( " +
				RESULT._ID + " INTEGER PRIMARY KEY, " +
				RESULT.DATE + " INTEGER, " +
				RESULT.LEVEL + " INTEGER, " +
				RESULT.SCORE + " INTEGER, " +
				RESULT.WEB_TYPE + " TEXT );";
		db.execSQL(sql);
	}

	public static long insert(SQLiteDatabase db, Result result) {
		ContentValues values = new ContentValues();
		values.put(RESULT.DATE, result.getDate());
		values.put(RESULT.LEVEL, result.getLevel());
		values.put(RESULT.SCORE, result.getScore());
		values.put(RESULT.WEB_TYPE, result.getWebType().toString());

		return db.insert(TABLE_NAME, null, values);
	}
}
