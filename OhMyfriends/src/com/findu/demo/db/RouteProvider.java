package com.findu.demo.db;

import com.findu.demo.route.Route;

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
import android.os.Environment;


public class RouteProvider extends ContentProvider{
	private SQLiteOpenHelper mOpenHelper;
	private static final String sDatabaseName = "route.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAMES = "route";
	public static final Uri FIND_ROUTE_URI = Uri.parse("content://findu/route");

	private static final UriMatcher URI_MATCHER;
	private static final int URI_MATCH_ROUTE = 0;

	public static final String[] SUGGEST_PROJECTION = new String[] { "_id",
			"user", "date", "starttime", "currenttime", "pointIndex" };

	private static final String ORDER_BY = "_id ASC";
	private static final int MAX_SUGGESTION_LONG_ENTRIES = 7;
	private static final String MAX_SUGGESTION_LONG_ENTRIES_STRING = Integer
			.valueOf(MAX_SUGGESTION_LONG_ENTRIES).toString();

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI("findu", TABLE_NAMES, URI_MATCH_ROUTE);

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
		if (match == URI_MATCH_ROUTE) {
			return "vnd.android.cursor.dir/route";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int match = URI_MATCHER.match(uri);
		Uri url = null;
		if (match == URI_MATCH_ROUTE) {
			long rowID = db.insert(TABLE_NAMES, "user", initialValues);
			if (rowID > 0) {
				url = ContentUris.withAppendedId(FIND_ROUTE_URI, rowID);
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
		SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"findu/locus.db", null);
		mOpenHelper.onCreate(db);
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

	public static class DatabaseHelper extends SQLiteOpenHelper {
		private Context mContext;
		private int mMaxID = 0;
		public DatabaseHelper(Context context) {
			super(context, sDatabaseName, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE routes (" + "id INTEGER PRIMARY KEY,"
					+ "name TEXT," + "startTime TEXT,"
					+ "endTime TEXT," + "pointIndex LONG,"
					 + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
		
		public void insertRoute(Route route)
		{
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("id", mMaxID++);
			values.put("name", route.mName);
			values.put("startTime", Route.dateFormat.format(route.mBegin));
			values.put("endTime", Route.dateFormat.format(route.mEnd));
			db.insert("routes", null, values);
			
		}
	}
}
