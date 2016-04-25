package com.alaske.demo.mvp.presenter.user;

import com.alaske.demo.mvp.mo.UserMo;

/**
 * Author: zhaocheng
 * Date: 2016-04-25
 * Time: 14:25
 * Name:IUserPresenter
 * Introduction:
 */
public interface IUserPresenter {

    void getUser(String id);

    void updateUser(UserMo mo);



}
