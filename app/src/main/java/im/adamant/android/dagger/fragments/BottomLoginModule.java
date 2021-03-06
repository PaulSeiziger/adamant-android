package im.adamant.android.dagger.fragments;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.adamant.android.Screens;
import im.adamant.android.interactors.AuthorizeInteractor;
import im.adamant.android.ui.presenters.LoginPresenter;
import io.reactivex.disposables.CompositeDisposable;
import ru.terrakok.cicerone.Router;

@Module
public class BottomLoginModule {
    @FragmentScope
    @Provides
    public static LoginPresenter provideLoginPresenter(
            Router router,
            AuthorizeInteractor interactor
    ){
        return new LoginPresenter(router, interactor);
    }
}
