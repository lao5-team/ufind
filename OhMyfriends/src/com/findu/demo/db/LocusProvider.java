package com.findu.demo.db;

import com.baidu.platform.comapi.map.q;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class LocusProvider extends ContentProvider {

	private SQLiteOpenHelper mOpenHelper;
	private static final String sDatabaseName = "mylocus.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAMES = "mylocus";
	public static final Uri FIND_URI = Uri.parse("content://findu/mylocus");

	private static final UriMatcher URI_MATCHER;
	private static final int URI_MATCH_LOCUS = 0;

	public static final String[] SUGGEST_PROJECTION = new String[] { "_id",
			"user", "date", "starttime", "currenttime", "latitude", "longitude" };

	private static final String ORDER_BY = "_id ASC";
	private static final int MAX_SUGGESTION_LONG_ENTRIES = 7;
	private static final String MAX_SUGGESTION_LONG_ENTRIES_STRING = Integer
			.valueOf(MAX_SUGGESTION_LONG_ENTRIES).toString();

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI("findu", TABLE_NAMES, URI_MATCH_LOCUS);

	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int match = URI_MATCHER.match(url);
		if (match == -1) {
			throw new IllegalArgumentException("Unknown URL");
		}

		ContentResolver cr = getContext().getContentResolver();

		int count = db.delete(TABLE_NAMES, where, whereArgs);
		cr.notifyChange(url, null);

		return count;
	}

	@Override
	public String getType(Uri uri) {
		int match = URI_MATCHER.match(uri);
		if (match == URI_MATCH_LOCUS) {
			return "vnd.android.cursor.dir/locus";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int match = URI_MATCHER.match(uri);
		Uri url = null;
		if (match == URI_MATCH_LOCUS) {
			long rowID = db.insert(TABLE_NAMES, "user", initialValues);
			if (rowID > 0) {
				url = ContentUris.withAppendedId(FIND_URI, rowID);
			}
		}

		if (url == null) {
			throw new IllegalArgumentException("Unknown URL");
		}
		getContext().getContentResolver().notifyChange(url, null);

		return url;
	}

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new DatabaseHelper(context);

		return false;
	}

	@Override
	public Cursor query(Uri url, String[] projectionIn, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query(TABLE_NAMES, projectionIn, null, selectionArgs,
				null, null, sortOrder, null);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		ContentResolver cr = getContext().getContentResolver();
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int ret = db.update(TABLE_NAMES, values, where, whereArgs);
		cr.notifyChange(url, null);
		return ret;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private Context mContext;

		public DatabaseHelper(Context context) {
			super(context, sDatabaseName, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE mylocus (" + "_id INTEGER PRIMARY KEY,"
					+ "user TEXT," + "date LONG," + "starttime LONG,"
					+ "currenttime LONG," + "latitude LONG,"
					+ "longitude LONG" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

}
