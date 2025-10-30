package com.wingstars.base.net;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Created by Milla on 2020/8/20.
 */
public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private Dialog pd;

    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;

    public ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super(Looper.getMainLooper());
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }

    private void initProgressDialog() {
        if (pd == null) {
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
//            LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
//            // main.xml中的ImageView
//            ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
//            TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
//            Animation animation = AnimationUtils.loadAnimation(context,R.anim.loading_anim);
//            spaceshipImage.startAnimation(animation);
//            // 加载动画
//            // GlideApp.with(context).asGif().load(R.mipmap.loding).into(spaceshipImage);
//
//            pd = new Dialog(context, R.style.loading_dialog);
//            pd.setContentView(layout, new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.FILL_PARENT,
//                    LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
//            pd.setCancelable(cancelable);
//
//            if (cancelable) {
//                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        mProgressCancelListener.onCancelProgress();
//                    }
//                });
//            }
//
//            if (!pd.isShowing() && isValidContext(context)) {
//                pd.show();
//            }
        }
    }

    private void dismissProgressDialog() {
        if (pd != null) {
            if (isValidContext(context) && pd.isShowing()) {
                pd.dismiss();
                pd = null;
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }


    private boolean isValidContext(Context c) {
        Activity a = (Activity) c;
        if (a.isDestroyed() || a.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }
}
