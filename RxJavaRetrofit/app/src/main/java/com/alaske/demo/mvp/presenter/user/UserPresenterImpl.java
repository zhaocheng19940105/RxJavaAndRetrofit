package com.alaske.demo.mvp.presenter.user;

import com.alaske.demo.App;
import com.alaske.demo.api.WebApiService;
import com.alaske.demo.api.webapi.UserResp;
import com.alaske.demo.mvp.mo.IUserMo;
import com.alaske.demo.mvp.mo.UserMo;
import com.alaske.demo.mvp.mo.UserMoImpl;
import com.alaske.demo.mvp.presenter.BasePresenter;
import com.alaske.demo.ui.IUserView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Author: zhaocheng
 * Date: 2016-04-25
 * Time: 14:24
 * Name:UserPresenter
 * Introduction:
 */
public class UserPresenterImpl extends BasePresenter implements IUserPresenter {

    private IUserView mIUserView;
    private IUserMo mIUserMo;

    public UserPresenterImpl(IUserView iUserView) {
        mIUserView = iUserView;
        mIUserMo = new UserMoImpl();
    }


    @Override
    public void getUser(String id) {
        mIUserView.showProgressDialog();
        App.apiService(WebApiService.class)
                .getUser(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<UserResp, Observable<UserMo>>() {
                    @Override
                    public  Observable<UserMo> call(UserResp userResp) {
                        return mIUserMo.addUserDB(userResp.data);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserMo>() {
                    @Override
                    public void onCompleted() {
                        mIUserView.hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIUserView.showError(e.toString());
                        mIUserView.hideProgressDialog();
                    }

                    @Override
                    public void onNext(UserMo mo) {
                        mIUserView.hideProgressDialog();
                        mIUserView.updateView(mo);
                    }
                });
    }



    @Override
    public void updateUser(UserMo mo) {

    }
}
