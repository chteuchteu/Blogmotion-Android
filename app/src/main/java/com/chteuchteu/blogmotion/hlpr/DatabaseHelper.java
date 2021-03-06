package com.chteuchteu.blogmotion.hlpr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.chteuchteu.blogmotion.obj.MusicPost;
import com.chteuchteu.blogmotion.obj.Post;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "blogmotion.db";

	// Tables names
	private static final String TABLE_POSTS = "posts";
	private static final String TABLE_MUSICPOSTS = "musicPosts";

	// Fields
	private static final String KEY_ID = "id";

	// Posts
	private static final String KEY_POSTS_TITLE = "title";
	private static final String KEY_POSTS_PUBLISHDATE = "publishDate";
	private static final String KEY_POSTS_PERMALINK = "permalink";
	private static final String KEY_POSTS_CATEGORIES = "categories";
	private static final String KEY_POSTS_DESCRIPTION = "description";
	private static final String KEY_POSTS_CONTENT = "content";

	private static final String KEY_MUSICPOSTS_TITLE = "title";
	private static final String KEY_MUSICPOSTS_TARGETURL = "targetUrl";
	private static final String KEY_MUSICPOSTS_PUBDATE = "publishDate";
	private static final String KEY_MUSICPOSTS_TYPE = "type";

	private static final String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_POSTS_TITLE + " TEXT, "
			+ KEY_POSTS_PUBLISHDATE + " TEXT, "
			+ KEY_POSTS_PERMALINK + " TEXT, "
			+ KEY_POSTS_CATEGORIES + " TEXT, "
			+ KEY_POSTS_DESCRIPTION + " TEXT, "
			+ KEY_POSTS_CONTENT + " TEXT)";

	private static final String CREATE_TABLE_MUSICPOSTS = "CREATE TABLE " + TABLE_MUSICPOSTS + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_MUSICPOSTS_TITLE + " TEXT, "
			+ KEY_MUSICPOSTS_TARGETURL + " TEXT, "
			+ KEY_MUSICPOSTS_PUBDATE + " TEXT, "
			+ KEY_MUSICPOSTS_TYPE + " TEXT)";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		if (context == null)
			throw new IllegalStateException();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_POSTS);
		db.execSQL(CREATE_TABLE_MUSICPOSTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

	public static void close(Cursor c, SQLiteDatabase db) {
		if (c != null) c.close();
		if (db != null) db.close();
	}


	/* POSTS */
	public void insertPosts(List<Post> posts) {
		SQLiteDatabase db = this.getWritableDatabase();

		for (Post post : posts) {
			ContentValues values = new ContentValues();
			values.put(KEY_POSTS_TITLE, post.getTitle());
			values.put(KEY_POSTS_PUBLISHDATE, post.getPublishDate());
			values.put(KEY_POSTS_PERMALINK, post.getPermalink());
			values.put(KEY_POSTS_CATEGORIES, post.getCategoriesAsString());
			values.put(KEY_POSTS_DESCRIPTION, post.getDescription());
			values.put(KEY_POSTS_CONTENT, post.getContent());

			long id = db.insert(TABLE_POSTS, null, values);
			post.setId(id);
		}

		close(null, db);
	}

	public void clearPosts() {
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("DELETE FROM " + TABLE_POSTS);

		close(null, db);
	}

	public List<Post> getPosts() {
		List<Post> posts = new ArrayList<>();

		String selectQuery = "SELECT * FROM " + TABLE_POSTS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			do {
				posts.add(new Post(
						c.getLong(c.getColumnIndex(KEY_ID)),
						c.getString(c.getColumnIndex(KEY_POSTS_TITLE)),
						c.getString(c.getColumnIndex(KEY_POSTS_PERMALINK)),
						c.getString(c.getColumnIndex(KEY_POSTS_PUBLISHDATE)),
						c.getString(c.getColumnIndex(KEY_POSTS_CATEGORIES)),
						c.getString(c.getColumnIndex(KEY_POSTS_DESCRIPTION)),
						c.getString(c.getColumnIndex(KEY_POSTS_CONTENT))));
			} while (c.moveToNext());
		}

		close(c, db);

		return posts;
	}

	public void deletePost(Post post) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_POSTS, KEY_ID + " = ?", new String[] { String.valueOf(post.getId()) });
		close(null, db);
	}


	/* MUSIC POSTS */
	public void insertMusicPosts(List<MusicPost> posts) {
		SQLiteDatabase db = this.getWritableDatabase();

		for (MusicPost post : posts) {
			ContentValues values = new ContentValues();
			values.put(KEY_MUSICPOSTS_TITLE, post.getTitle());
			values.put(KEY_MUSICPOSTS_TARGETURL, post.getPermalink());
			values.put(KEY_MUSICPOSTS_PUBDATE, post.getPublishDate());
			values.put(KEY_MUSICPOSTS_TYPE, post.getType().toString());

			long id = db.insert(TABLE_MUSICPOSTS, null, values);
			post.setId(id);
		}

		close(null, db);
	}

	public void clearMusicPosts() {
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("DELETE FROM " + TABLE_MUSICPOSTS);

		close(null, db);
	}

	public List<MusicPost> getMusicPosts() {
		List<MusicPost> posts = new ArrayList<>();

		String selectQuery = "SELECT * FROM " + TABLE_MUSICPOSTS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			do {
				posts.add(new MusicPost(
						c.getLong(c.getColumnIndex(KEY_ID)),
						c.getString(c.getColumnIndex(KEY_MUSICPOSTS_TITLE)),
						c.getString(c.getColumnIndex(KEY_MUSICPOSTS_TARGETURL)),
						c.getString(c.getColumnIndex(KEY_MUSICPOSTS_PUBDATE)),
						MusicPost.MusicPostType.get(c.getString(c.getColumnIndex(KEY_MUSICPOSTS_TYPE)))));
			} while (c.moveToNext());
		}

		close(c, db);

		return posts;
	}

	public boolean hasPosts() {
		try {
			return GenericQueries.getNbLines(this, TABLE_POSTS, "") > 0;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
			Crashlytics.logException(ex);
			return false;
		}
	}
	public boolean hasMusicPosts() {
		try {
			return GenericQueries.getNbLines(this, TABLE_MUSICPOSTS, "") > 0;
		} catch (SQLiteException ex) {
			ex.printStackTrace();
			Crashlytics.logException(ex);
			return false;
		}
	}

	public static class GenericQueries {
		/**
		 * Returns the number of lines returned by a SELECT COUNT(*) FROM _table WHERE _cond
		 * Ex : where = 'ID = 5'
		 * @param table Table name
		 * @param where Where condition (ID = 5)
		 * @return int nbLines
		 */
		public static int getNbLines(SQLiteOpenHelper sqloh, String table, String where) {
			String query = "SELECT COUNT(*) FROM " + table;
			if (!where.equals(""))
				query += " WHERE " + where;
			SQLiteDatabase db = sqloh.getReadableDatabase();
			Cursor cursor = db.rawQuery(query, null);
			try {
				cursor.moveToNext();
				int val = cursor.getInt(0);
				cursor.close();
				return val;
			} catch (Exception e) { e.printStackTrace(); }
			return 0;
		}
	}
}
