package com.wingstars.base.net;

/**
 * Created by Milla on 2020/8/20.
 */
public interface SubscriberHttpListener<T>  {
    void onNext(T t);
    void onError(Throwable error);
}
