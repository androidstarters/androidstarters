package io.sample.app.features.detail;

import io.sample.app.data.model.response.Pokemon;
import io.sample.app.data.model.response.Statistic;
import io.sample.app.features.base.MvpView;

public interface DetailMvpView extends MvpView {

    void showPokemon(Pokemon pokemon);

    void showStat(Statistic statistic);

    void showProgress(boolean show);

    void showError(Throwable error);
}
