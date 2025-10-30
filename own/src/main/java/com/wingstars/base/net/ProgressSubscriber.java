package com.wingstars.base.net;

import android.content.Context;

import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.HttpException;


/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by Milla on 2020/8/20.
 */
public class ProgressSubscriber<T> implements ProgressCancelListener, Observer<T> {

    private SubscriberHttpListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;

    Disposable mDisposable;


    public ProgressSubscriber(SubscriberHttpListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public ProgressSubscriber(SubscriberHttpListener mSubscriberOnNextListener) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
//        this.context = context;
        // mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        String errorMsg = "";
//        if (e instanceof HttpException){
//            if(((HttpException) e).code() == 401){
//                errorMsg = "网络连接错误";
//            }else if(((HttpException) e).code() == 500){
//                errorMsg = "网络连接错误";
//            }else if(((HttpException) e).code() == 502){
//                errorMsg = "网络连接错误";
//            }else{
//                errorMsg = e.getMessage();
//            }
//        }else if (e instanceof SocketTimeoutException) {
//            errorMsg = "网络连接错误";
//        } else if (e instanceof ConnectException) {
//            errorMsg = "网络连接错误";
//        } else if (e instanceof ConnectTimeoutException) {
//            errorMsg = "网络连接错误";
//        } else if (e instanceof UnknownHostException) {
//            errorMsg = "网络连接错误";
//        } else if (e instanceof SSLHandshakeException) {
//            errorMsg = "网络连接错误";
//        } else if (e instanceof MalformedJsonException){
//            errorMsg = "网络连接错误";
//        } else {
//            errorMsg = e.getMessage();
//        }

        errorMsg = e.getMessage();

        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
        }
        dismissProgressDialog();
    }



    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    @Override
    public void onSubscribe(io.reactivex.rxjava3.disposables.@NonNull Disposable d) {
        showProgressDialog();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }
    }
}