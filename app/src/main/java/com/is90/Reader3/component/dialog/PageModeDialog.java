package com.is90.Reader3.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.is90.Reader3.Config;
import com.is90.Reader3.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class PageModeDialog extends Dialog {

    @Bind(R.id.tv_simulation)
    TextView tv_simulation;
    @Bind(R.id.tv_cover)
    TextView tv_cover;
    @Bind(R.id.tv_slide)
    TextView tv_slide;
    @Bind(R.id.tv_none)
    TextView tv_none;
    @Bind(R.id.tv_autoread)
    TextView tv_autoread;
    @Bind(R.id.tv_speed)
    TextView tv_speed;
    @Bind(R.id.ll_speed)
    LinearLayout ll_speed;

    private Config config;
    private PageModeListener pageModeListener;
    private int MAX_AUTOREAD_SPEED =  30;
    private int MIN_AUTOREAD_SPEED = 5;
    private int currentAutoReadSpeed = 15;

    private PageModeDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public PageModeDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public PageModeDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_pagemode);
        // 初始化View注入
        ButterKnife.bind(this);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        config = Config.getInstance();
        selectPageMode(config.getPageMode());

        if(config.getAutoRead()){
            setTextViewSelect(tv_autoread,true);
            tv_autoread.setText("退出自动阅读");
            ll_speed.setVisibility(View.VISIBLE);
        }else {
            setTextViewSelect(tv_autoread,false);
            tv_autoread.setText("自动阅读");
            ll_speed.setVisibility(View.GONE);
        }
        currentAutoReadSpeed = config.getAutoReadSpeed();
        tv_speed.setText(currentAutoReadSpeed+"秒");
    }


    @OnClick({R.id.tv_simulation, R.id.tv_cover, R.id.tv_slide, R.id.tv_none,R.id.tv_autoread,R.id.tv_subtract,R.id.tv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_simulation:
                selectPageMode(Config.PAGE_MODE_SIMULATION);
                setPageMode(Config.PAGE_MODE_SIMULATION);
                break;
            case R.id.tv_cover:
                selectPageMode(Config.PAGE_MODE_COVER);
                setPageMode(Config.PAGE_MODE_COVER);
                break;
            case R.id.tv_slide:
                selectPageMode(Config.PAGE_MODE_SLIDE);
                setPageMode(Config.PAGE_MODE_SLIDE);
                break;
            case R.id.tv_none:
                selectPageMode(Config.PAGE_MODE_NONE);
                setPageMode(Config.PAGE_MODE_NONE);
                break;
            case R.id.tv_autoread:
                if(config.getAutoRead()){
                    setTextViewSelect(tv_autoread,false);
                    config.setAutoRead(false);
                    tv_autoread.setText("自动阅读");
                    ll_speed.setVisibility(View.GONE);
                }else {
                    setTextViewSelect(tv_autoread,true);
                    tv_autoread.setText("退出自动阅读");
                    config.setAutoRead(true);
                    ll_speed.setVisibility(View.VISIBLE);
                }
                if (pageModeListener != null) {
                    pageModeListener.changeAutoRead(config.getAutoRead(),config.getAutoReadSpeed());
                }
                break;
            case R.id.tv_add:
                addAutoReadSpeed();
                break;
            case R.id.tv_subtract:
                subtractAutoReadSpeed();
                break;
        }
    }

    //变大书本字体
    private void addAutoReadSpeed() {
        if (currentAutoReadSpeed < MAX_AUTOREAD_SPEED) {
            currentAutoReadSpeed += 1;
            tv_speed.setText(currentAutoReadSpeed + "秒");
            config.setAutoReadSpeed(currentAutoReadSpeed);
            if (pageModeListener != null) {
                pageModeListener.changeAutoRead(config.getAutoRead(),currentAutoReadSpeed);
            }
        }
    }

    //变小书本字体
    private void subtractAutoReadSpeed() {
        if (currentAutoReadSpeed > MIN_AUTOREAD_SPEED) {
            currentAutoReadSpeed -= 1;
            tv_speed.setText(currentAutoReadSpeed + "秒");
            config.setAutoReadSpeed(currentAutoReadSpeed);
            if (pageModeListener != null) {
                pageModeListener.changeAutoRead(config.getAutoRead(),currentAutoReadSpeed);
            }
        }
    }

    //设置翻页
    public void setPageMode(int pageMode) {
        config.setPageMode(pageMode);
        if (pageModeListener != null) {
            pageModeListener.changePageMode(pageMode);
        }
    }
    //选择怕翻页
    private void selectPageMode(int pageMode) {
        if (pageMode == Config.PAGE_MODE_SIMULATION) {
            setTextViewSelect(tv_simulation, true);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_COVER) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, true);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_SLIDE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, true);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_NONE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, true);
        }

    }

    //设置按钮选择的背景
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_select_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.read_dialog_button_select));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    public void setPageModeListener(PageModeListener pageModeListener) {
        this.pageModeListener = pageModeListener;
    }

    public interface PageModeListener {
        void changePageMode(int pageMode);
        void changeAutoRead(boolean autoRead,int speed);
    }
}
