
package com.is90.Reader3.component;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.is90.Reader3.R;

/**
 * @author yuyh.
 * @date 16/9/5.
 */
public class LoginPopupWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Activity mActivity;

    private ImageView qq;
    private ImageView wechat;

    LoginTypeListener listener;


    public LoginPopupWindow(Activity activity) {
        mActivity = activity;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mContentView = LayoutInflater.from(activity).inflate(R.layout.layout_login_popup_window, null);
        setContentView(mContentView);

        qq = (ImageView) mContentView.findViewById(R.id.ivQQ);
        wechat = (ImageView) mContentView.findViewById(R.id.ivWechat);

        qq.setOnClickListener(this);
        wechat.setOnClickListener(this);

        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        setAnimationStyle(R.style.LoginPopup);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                lighton();
            }
        });
    }


    private void lighton() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        mActivity.getWindow().setAttributes(lp);
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        lightoff();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        lightoff();
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.ivQQ:
            listener.onLogin(qq, "QQ");
            break;
        case R.id.ivWechat:
            listener.onLogin(wechat, "WEICHAT");
            break;

        }
    }

    public interface LoginTypeListener {

        void onLogin(ImageView view, String type);
    }

    public void setLoginTypeListener(LoginTypeListener listener){
        this.listener = listener;
    }

}
