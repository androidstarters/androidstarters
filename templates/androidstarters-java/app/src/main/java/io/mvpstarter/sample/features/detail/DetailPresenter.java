package <%= appPackage %>.features.detail;

import javax.inject.Inject;

import <%= appPackage %>.data.DataManager;
import <%= appPackage %>.data.model.response.Statistic;
import <%= appPackage %>.features.base.BasePresenter;
import <%= appPackage %>.injection.ConfigPersistent;
import <%= appPackage %>.util.rx.scheduler.SchedulerUtils;

@ConfigPersistent
public class DetailPresenter extends BasePresenter<DetailMvpView> {

    private final DataManager dataManager;

    @Inject
    public DetailPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(DetailMvpView mvpView) {
        super.attachView(mvpView);
    }

    public void getPokemon(String name) {
        checkViewAttached();
        getView().showProgress(true);
        dataManager
                .getPokemon(name)
                .compose(SchedulerUtils.ioToMain())
                .subscribe(
                        pokemon -> {
                            // It should be always checked if MvpView (Fragment or Activity) is attached.
                            // Calling showProgress() on a not-attached fragment will throw a NPE
                            // It is possible to ask isAdded() in the fragment, but it's better to ask in the presenter
                            getView().showProgress(false);
                            getView().showPokemon(pokemon);
                            for (Statistic statistic : pokemon.stats) {
                                getView().showStat(statistic);
                            }
                        },
                        throwable -> {
                            getView().showProgress(false);
                            getView().showError(throwable);
                        });
    }
}
