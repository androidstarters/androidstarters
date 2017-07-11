package io.sample.app.common.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import io.sample.app.common.injection.module.ApplicationTestModule;
import io.sample.app.injection.component.AppComponent;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends AppComponent {
}
