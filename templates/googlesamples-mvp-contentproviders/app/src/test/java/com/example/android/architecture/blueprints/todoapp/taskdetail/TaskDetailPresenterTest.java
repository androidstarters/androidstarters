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

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import <%= appPackage %>.data.Task;
import <%= appPackage %>.data.source.LoaderProvider;
import <%= appPackage %>.data.source.MockCursorProvider;
import <%= appPackage %>.data.source.TasksRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link TaskDetailPresenter}
 */
public class TaskDetailPresenterTest {

    public static final String TITLE_TEST = "TITLE";
    public static final String DESCRIPTION_TEST = "DESCRIPTION";
    public static final String INVALID_TASK_ID = "";

    @Mock
    private TaskDetailContract.View mTaskDetailFragment;

    @Mock
    private LoaderProvider mLoaderProvider;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private TasksRepository mTasksRepository;

    private MockCursorProvider.TaskMockCursor mActiveTaskCursor;
    private MockCursorProvider.TaskMockCursor mCompletedTaskCursor;
    private Task mActiveTask;
    private Task mCompletedTask;

    private TaskDetailPresenter mTaskDetailPresenter;

    @Before
    public void setup() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mActiveTaskCursor = MockCursorProvider.createActiveTaskCursor();
        mCompletedTaskCursor = MockCursorProvider.createCompletedTaskCursor();

        mActiveTask = Task.from(mActiveTaskCursor);
        mCompletedTask = Task.from(mCompletedTaskCursor);
    }

    @Test
    public void getActiveTaskFromRepositoryAndLoadIntoView() {
        // Get a reference to the class under test
        mTaskDetailPresenter = new TaskDetailPresenter(
                mActiveTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository, mTaskDetailFragment
        );

        // When tasks presenter is asked to open an ACTIVE_TASK
        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mActiveTaskCursor);

        // Then progress indicator is hidden and title, description and completion status are shown
        // in UI
        verify(mTaskDetailFragment).setLoadingIndicator(false);
        verify(mTaskDetailFragment).showTitle(TITLE_TEST);
        verify(mTaskDetailFragment).showDescription(DESCRIPTION_TEST);
    }

    @Test
    public void getCompletedTaskFromRepositoryAndLoadIntoView() {
        // When tasks presenter is asked to open a completed task
        mTaskDetailPresenter = new TaskDetailPresenter(mCompletedTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );

        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mCompletedTaskCursor);

        // Then progress indicator is hidden and title, description and completion status are shown
        // in UI
        verify(mTaskDetailFragment).setLoadingIndicator(false);
        verify(mTaskDetailFragment).showTitle(TITLE_TEST);
        verify(mTaskDetailFragment).showDescription(DESCRIPTION_TEST);
        verify(mTaskDetailFragment).showCompletionStatus(true);
    }

    @Test
    public void getUnknownTaskFromRepositoryAndLoadIntoView() {
        // When loading of an ACTIVE_TASK is requested with an invalid task
        mTaskDetailPresenter = new TaskDetailPresenter(INVALID_TASK_ID, mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );

        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), null);

        // Then progress indicator is hidden and missing ACTIVE_TASK UI is shown
        verify(mTaskDetailFragment).showMissingTask();
    }

    @Test
    public void deleteTask() {
        // When the deletion of an ACTIVE_TASK is requested
        mTaskDetailPresenter = new TaskDetailPresenter(mActiveTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );

        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mActiveTaskCursor);

        mTaskDetailPresenter.deleteTask();

        // Then the repository and the view are notified
        verify(mTasksRepository).deleteTask(mActiveTask.getId());
        verify(mTaskDetailFragment).showTaskDeleted();
    }

    @Test
    public void completeTask() {
        // When the presenter is asked to complete the ACTIVE_TASK
        mTaskDetailPresenter = new TaskDetailPresenter(
                mActiveTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );
        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mActiveTaskCursor);

        mTaskDetailPresenter.completeTask();

        // Then a request is sent to the repository and the UI is updated
        verify(mTasksRepository).completeTask(mActiveTask);
        verify(mTaskDetailFragment).showTaskMarkedComplete();
    }

    @Test
    public void activateTask() {
        // When the presenter is asked to activate the ACTIVE_TASK
        Task completedTask = Task.from(mCompletedTaskCursor);

        mTaskDetailPresenter = new TaskDetailPresenter(
                completedTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );

        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mCompletedTaskCursor);

        mTaskDetailPresenter.activateTask();

        // Then a request is sent to the repository and the UI is updated
        verify(mTasksRepository).activateTask(completedTask);
        verify(mTaskDetailFragment).showTaskMarkedActive();
    }

    @Test
    public void activeTaskIsShownWhenEditing() {
        // When the edit of an ACTIVE_TASK is requested
        mTaskDetailPresenter = new TaskDetailPresenter(
                mActiveTask.getId(), mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );

        mTaskDetailPresenter.onLoadFinished(mock(Loader.class), mActiveTaskCursor);

        mTaskDetailPresenter.editTask();

        // Then the view is notified
        verify(mTaskDetailFragment).showEditTask(mActiveTask.getId());
    }

    @Test
    public void invalidTaskIsNotShownWhenEditing() {
        // When the edit of an invalid task id is requested
        mTaskDetailPresenter = new TaskDetailPresenter(INVALID_TASK_ID, mLoaderProvider, mLoaderManager, mTasksRepository,
                mTaskDetailFragment
        );
        mTaskDetailPresenter.editTask();

        // Then the edit mode is never started
        verify(mTaskDetailFragment, never()).showEditTask(INVALID_TASK_ID);
        // instead, the error is shown.
        verify(mTaskDetailFragment).showMissingTask();
    }

}


