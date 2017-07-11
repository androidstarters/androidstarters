package io.sample.app.runner;

import android.app.Application;
import android.content.Context;

import io.appflate.restmock.android.RESTMockTestRunner;
import io.sample.app.MvpStarterApplication;

/**
 * Created by ravindra on 4/2/17.
 */
public class TestRunner extends RESTMockTestRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, MvpStarterApplication.class.getName(), context);
    }
}
