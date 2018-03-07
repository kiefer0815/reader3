package com.is90.Reader3.ui;

/**
 * Created by kiefer on 2017/6/16.
 */

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import com.is90.Reader3.MainActivity;
import com.is90.Reader3.R;
import com.is90.Reader3.databinding.ActivityTransitionBinding;
import com.is90.Reader3.utils.PerfectClickListener;

public class TransitionActivity extends AppCompatActivity {

        private ActivityTransitionBinding mBinding;
        private boolean animationEnd;
        private boolean isIn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transition);

//                // 先显示默认图
//                mBinding.ivDefultPic.setImageDrawable(CommonUtils.getDrawable(R.drawable.img_transition_default));
//                Glide.with(this)
//                        .load("")
//                        .placeholder(R.drawable.img_transition_default)
//                        .error(R.drawable.img_transition_default)
//                        .into(mBinding.ivPic);

//                new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                                mBinding.ivDefultPic.setVisibility(View.GONE);
//                        }
//                }, 500);

                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                toMainActivity();
                        }
                }, 500);

                //        Animation animation = AnimationUtils.loadAnimation(this, R.anim.transition_anim);
                //        animation.setAnimationListener(animationListener);
                //        mBinding.ivPic.startAnimation(animation);

                mBinding.tvJump.setOnClickListener(new PerfectClickListener() {
                        @Override
                        protected void onNoDoubleClick(View v) {
                                toMainActivity();
                                //                animationEnd();
                        }
                });
        }

        /**
         * 实现监听跳转效果
         */
        private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                        animationEnd();
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
        };


        private void animationEnd() {
                synchronized (TransitionActivity.this) {
                        if (!animationEnd) {
                                animationEnd = true;
                                mBinding.ivPic.clearAnimation();
                                toMainActivity();
                        }
                }
        }

        private void toMainActivity() {
                if (isIn) {
                        return;
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                finish();
                isIn = true;
        }
}

