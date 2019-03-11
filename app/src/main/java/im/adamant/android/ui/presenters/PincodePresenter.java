package im.adamant.android.ui.presenters;

import android.os.Bundle;

import com.arellomobile.mvp.InjectViewState;

import im.adamant.android.R;
import im.adamant.android.helpers.LoggerHelper;
import im.adamant.android.interactors.SecurityInteractor;
import im.adamant.android.ui.mvp_view.PinCodeView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class PincodePresenter extends BasePresenter<PinCodeView> {
    private SecurityInteractor pinCodeInteractor;
    private PinCodeView.MODE mode = PinCodeView.MODE.VERIFY;
    private int counter;
    private boolean ignoreInput = false;

    public PincodePresenter(SecurityInteractor pinCodeInteractor, CompositeDisposable subscriptions) {
        super(subscriptions);
        this.pinCodeInteractor = pinCodeInteractor;
    }

    public PincodePresenter(CompositeDisposable subscriptions) {
        super(subscriptions);
    }

    public void setMode(PinCodeView.MODE mode) {
        this.mode = mode;
        switch (mode){
            case CREATE: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_new_pincode);
            }
            break;
            case VERIFY: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_pincode);
            }
            break;
            case DROP: {
                getViewState().setSuggestion(R.string.activity_pincode_enter_pincode);
            }
            break;
        }
    }

    public void onInputPincodeWasCompleted(String pinCode) {
        if (ignoreInput){ return; }
        counter++;
        LoggerHelper.e("COUNTER", Integer.toString(counter));
        //TODO: Validation
        ignoreInput = true;
        switch (mode){
            case CREATE: {
                Disposable subscription = pinCodeInteractor
                        .savePassphrase(pinCode)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(
                                () -> {
                                    ignoreInput = false;
                                    getViewState().close();
                                },
                                error -> {
                                    ignoreInput = false;
                                    getViewState().showError(R.string.encryption_error);
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
                subscriptions.add(subscription);
            }
            break;
            case VERIFY: {
                Disposable subscription = pinCodeInteractor
                        .restoreAuthorizationByPincode(pinCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (authorization) -> {
                                    ignoreInput = false;
                                    if (authorization.isSuccess()) {
                                        getViewState().goToMain();
                                    } else {
                                        getViewState().showError(R.string.account_not_found);
                                    }
                                },
                                error -> {
                                    ignoreInput = false;
                                    getViewState().showError(R.string.wrong_pincode);
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
                subscriptions.add(subscription);
            }
            break;
            case DROP: {
                Disposable subscription = pinCodeInteractor
                        .validatePincode(pinCode)
                        .ignoreElement()
                        .andThen(pinCodeInteractor.dropPassphrase())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    ignoreInput = false;
                                    getViewState().close();
                                },
                                error -> {
                                    ignoreInput = false;
                                    getViewState().showError(R.string.wrong_pincode);
                                    LoggerHelper.e("PINCODE", error.getMessage(), error);
                                }
                        );
                subscriptions.add(subscription);
            }
            break;
        }
    }
}