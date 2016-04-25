package com.alaske.demo.mvp.mo;

import rx.Observable;

/**
 * Author: zhaocheng
 * Date: 2016-04-25
 * Time: 15:44
 * Name:IUserMo
 * Introduction:
 */
public interface IUserMo {

    Observable<UserMo> addUserDB(UserMo mo);


}
