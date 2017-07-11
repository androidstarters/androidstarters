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

package <%= appPackage %>.taskdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import <%= appPackage %>.data.Task;
import <%= appPackage %>.data.source.LoaderProvider;
import <%= appPackage %>.data.source.TasksDataSource;
import <%= appPackage %>.data.source.TasksRepository;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;



/**
 * Listens to user actions from the UI ({@link TaskDetailFragment}), retrieves the data and updates
 * the UI as required.
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter, LoaderManager.LoaderCallbacks<Cursor>,TasksDataSource.GetTaskCallback {

    public final static int TASK_LOADER = 2;

    @NonNull
    private final TasksRepository mTasksRepository;
    private TaskDetailContract.View mTaskDetailView;
    private LoaderProvider mLoaderProvider;
    private LoaderManager mLoaderManager;
    private Task mTask;
    private String mTaskId;

    public TaskDetailPresenter(@NonNull String taskId,
                               @NonNull LoaderProvider loaderProvider,
                               @NonNull LoaderManager loaderManager,
                               @NonNull TasksRepository tasksRepository,
                               @NonNull TaskDetailContract.View taskDetailView) {
        mTaskId = checkNotNull(taskId, "taskId cannot be null!");
        mLoaderProvider = checkNotNull(loaderProvider, "loaderProvider cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTask();
    }

    private void loadTask() {
        mTaskDetailView.setLoadingIndicator(true);
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public void editTask() {
        if (null == mTask) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskDetailView.showEditTask(mTask.getId());
    }

    @Override
    public void deleteTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    public void completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.completeTask(mTask);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.activateTask(mTask);
        mTaskDetailView.showTaskMarkedActive();
    }

    private void showTask(Cursor data) {
        mTask = Task.from(data);

        String title = mTask.getTitle();
        String description = mTask.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(mTask.isCompleted());
        mTaskDetailView.setLoadingIndicator(false);
    }

    public void onDataLoaded(Cursor data) {
        showTask(data);
    }

    public void onDataEmpty() {
        mTaskDetailView.showMissingTask();
    }

    @Override
    public void onTaskLoaded(Task task) {
        // the data is refreshed locally now but
        // we don't need this result since the CursorLoader will load it for us
        mLoaderManager.initLoader(TASK_LOADER, null, this);
    }

    public void onDataNotAvailable() {
        mTaskDetailView.showMissingTask();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createTaskLoader(mTaskId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }
}
