package com.vishnus1224.simplyrss.feedlibrary.repository.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.vishnus1224.simplyrss.feedlibrary.Feed
import java.util.*
import kotlin.collections.ArrayList

private const val TABLE_NAME_FEED = "Feed"
private const val COLUMN_NAME_FEED_ID = "FeedId"
private const val COLUMN_NAME_FEED_TITLE = "FeedTitle"
private const val COLUMN_NAME_FEED_URL = "FeedUrl"
private const val COLUMN_NAME_FEED_DESCRIPTION = "FeedDescription"
private const val COLUMN_NAME_FEED_CREATION_DATE = "FeedCreationDate"

private const val CREATE_FEED_TABLE = "CREATE TABLE $TABLE_NAME_FEED " +
        "(" +
        "$COLUMN_NAME_FEED_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
        "$COLUMN_NAME_FEED_TITLE TEXT NOT NULL," +
        "$COLUMN_NAME_FEED_URL TEXT NOT NULL UNIQUE," +
        "$COLUMN_NAME_FEED_DESCRIPTION TEXT NOT NULL," +
        "$COLUMN_NAME_FEED_CREATION_DATE LONG" +
        ")"

internal class SqliteFeedDatabase(val sqLiteOpenHelper: SQLiteOpenHelper) : FeedDatabase {

    companion object {
        fun createTables(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_FEED_TABLE)
        }
    }

    override fun saveFeed(feed: Feed): Either<FeedDatabaseException, DbFeed> {
        val database = sqLiteOpenHelper.writableDatabase
        val creationDate = Date().time
        val id = database.insert(TABLE_NAME_FEED, null, feed.toContentValues(creationDate))
        database.close()

        return if (id == -1L) {
            FeedDatabaseException("Could not insert feed : $feed into database").left()
        } else {
            feed.toDbFeed(id, creationDate).right()
        }
    }

    override fun getAllFeeds(): List<DbFeed> {
        val database = sqLiteOpenHelper.readableDatabase
        val cursor = database.rawQuery(
            "SELECT * FROM $TABLE_NAME_FEED ORDER BY $COLUMN_NAME_FEED_CREATION_DATE DESC",
            null
        )

        val feedList: ArrayList<DbFeed> = arrayListOf()
        if (cursor.moveToFirst()) {
            do {
                feedList.add(cursor.toDbFeed())
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()

        return feedList
    }

    override fun deleteFeed(feed: Feed): Either<FeedDatabaseException, Unit> {
        val database = sqLiteOpenHelper.writableDatabase

        val deletedRowCount = database.delete(
            TABLE_NAME_FEED, "$COLUMN_NAME_FEED_ID = ${feed.id}", null
        )

        database.close()

        return if (deletedRowCount == 0) {
            FeedDatabaseException("Could not delete feed : $feed").left()
        } else {
            Unit.right()
        }
    }
}

private fun Feed.toContentValues(creationDate: Long) = ContentValues().apply {
    put(COLUMN_NAME_FEED_TITLE, title)
    put(COLUMN_NAME_FEED_URL, feedUrl)
    put(COLUMN_NAME_FEED_DESCRIPTION, description)
    put(COLUMN_NAME_FEED_CREATION_DATE, creationDate)
}

private fun Feed.toDbFeed(insertionId: Long, creationDate: Long) = DbFeed(
    id = insertionId, title = title, url = feedUrl, description = description, creationDate = creationDate
)

private fun Cursor.toDbFeed() = DbFeed(
    id = getLong(getColumnIndex(COLUMN_NAME_FEED_ID)),
    title = getString(getColumnIndex(COLUMN_NAME_FEED_TITLE)),
    url = getString(getColumnIndex(COLUMN_NAME_FEED_URL)),
    description = getString(getColumnIndex(COLUMN_NAME_FEED_DESCRIPTION)),
    creationDate = getLong(getColumnIndex(COLUMN_NAME_FEED_CREATION_DATE))
)