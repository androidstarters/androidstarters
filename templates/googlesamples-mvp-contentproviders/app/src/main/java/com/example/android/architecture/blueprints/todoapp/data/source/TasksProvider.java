/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package <%= appPackage %>.data.source;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import <%= appPackage %>.data.source.local.TasksDbHelper;
import <%= appPackage %>.data.source.local.TasksPersistenceContract;

public class TasksProvider extends ContentProvider {

    private static final int TASK = 100;
    private static final int TASK_ITEM = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TasksDbHelper mTasksDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TasksPersistenceContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TasksPersistenceContract.TaskEntry.TABLE_NAME, TASK);
        matcher.addURI(authority, TasksPersistenceContract.TaskEntry.TABLE_NAME + "/*", TASK_ITEM);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mTasksDbHelper = new TasksDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return TasksPersistenceContract.CONTENT_TASK_TYPE;
            case TASK_ITEM:
                return TasksPersistenceContract.CONTENT_TASK_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TASK:
                retCursor = mTasksDbHelper.getReadableDatabase().query(
                        TasksPersistenceContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TASK_ITEM:
                String[] where = {uri.getLastPathSegment()};
                retCursor = mTasksDbHelper.getReadableDatabase().query(
                        TasksPersistenceContract.TaskEntry.TABLE_NAME,
                        projection,
                        TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mTasksDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASK:
                Cursor exists = db.query(
                        TasksPersistenceContract.TaskEntry.TABLE_NAME,
                        new String[]{TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID},
                        TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                        new String[]{values.getAsString(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID)},
                        null,
                        null,
                        null
                );
                if (exists.moveToLast()) {
                    long _id = db.update(
                            TasksPersistenceContract.TaskEntry.TABLE_NAME, values,
                            TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?",
                            new String[]{values.getAsString(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID)}
                    );
                    if (_id > 0) {
                        returnUri = TasksPersistenceContract.TaskEntry.buildTasksUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } else {
                    long _id = db.insert(TasksPersistenceContract.TaskEntry.TABLE_NAME, null, values);
                    if (_id > 0) {
                        returnUri = TasksPersistenceContract.TaskEntry.buildTasksUriWith(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                exists.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTasksDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case TASK:
                rowsDeleted = db.delete(
                        TasksPersistenceContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mTasksDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TASK:
                rowsUpdated = db.update(TasksPersistenceContract.TaskEntry.TABLE_NAME, values, selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
