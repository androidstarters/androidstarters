package io.sample.app.injection.component;

import dagger.Subcomponent;
import io.sample.app.injection.PerFragment;
import io.sample.app.injection.module.FragmentModule;

/**
 * This component inject dependencies to all Fragments across the application
 */
@PerFragment
@Subcomponent(modules = FragmentModule.class)
public interface FragmentComponent {
}
