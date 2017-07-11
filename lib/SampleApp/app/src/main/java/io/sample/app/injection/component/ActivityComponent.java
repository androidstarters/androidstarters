package io.sample.app.injection.component;

import dagger.Subcomponent;
import io.sample.app.features.detail.DetailActivity;
import io.sample.app.features.main.MainActivity;
import io.sample.app.injection.PerActivity;
import io.sample.app.injection.module.ActivityModule;

@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

    void inject(DetailActivity detailActivity);
}
