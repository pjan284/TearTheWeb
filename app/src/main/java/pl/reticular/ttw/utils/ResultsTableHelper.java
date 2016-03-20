package pl.reticular.ttw.utils;

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
