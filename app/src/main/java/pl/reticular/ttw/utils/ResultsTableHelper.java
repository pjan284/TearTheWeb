package pl.reticular.ttw.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import pl.reticular.ttw.game.Result;

public class ResultsTableHelper {
	public static class RESULT implements BaseColumns {
		public static final String TABLE_NAME = "Results";
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_LEVEL = "level";
		public static final String COLUMN_SCORE = "score";
		public static final String COLUMN_WEB_TYPE = "web_type";
	}

	public static void createTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + RESULT.TABLE_NAME + " ( " +
				RESULT._ID + " INTEGER PRIMARY KEY, " +
				RESULT.COLUMN_DATE + " INTEGER, " +
				RESULT.COLUMN_LEVEL + " INTEGER, " +
				RESULT.COLUMN_SCORE + " INTEGER, " +
				RESULT.COLUMN_WEB_TYPE + " TEXT );";
		db.execSQL(sql);
	}

	public static long insert(SQLiteDatabase db, Result result) {
		ContentValues values = new ContentValues();
		values.put(RESULT.COLUMN_DATE, result.getDate());
		values.put(RESULT.COLUMN_LEVEL, result.getLevel());
		values.put(RESULT.COLUMN_SCORE, result.getScore());
		values.put(RESULT.COLUMN_WEB_TYPE, result.getWebType().toString());

		return db.insert(RESULT.TABLE_NAME, null, values);
	}
}
