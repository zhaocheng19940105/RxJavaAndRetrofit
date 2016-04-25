package com.alaske.demo.mvp.mo;

import rx.Observable;

/**
 * Author: zhaocheng
 * Date: 2016-04-25
 * Time: 15:47
 * Name:UserMoImpl
 * Introduction: 此类是关于所有用户信息的本地操作 本地缓存 本地读取 等操作
 */
public class UserMoImpl implements IUserMo {

    /**
     * 用户信息添加到 数据库
     * @param mo
     * @return
     */

    @Override
    public Observable<UserMo> addUserDB(UserMo mo) {
        // 添加数据库的操作
        return Observable.just(mo);
    }
}
