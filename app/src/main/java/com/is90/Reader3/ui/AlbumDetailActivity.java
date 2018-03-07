package com.is90.Reader3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.base.BaseDataBindingActivity;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.AlbumBean;
import com.is90.Reader3.databinding.ActivityAlbumDetailBinding;
import com.is90.Reader3.fragment.BookRankListFragment;

/**
 * Created by kiefer on 2017/8/2.
 */

public class AlbumDetailActivity extends BaseDataBindingActivity<ActivityAlbumDetailBinding> {
        public static final String EXTRA_PARAM = "albumBean";
        private AlbumBean mAlbumBean;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_album_detail);
                if (getIntent() != null) {
                        mAlbumBean = (AlbumBean) getIntent().getSerializableExtra(EXTRA_PARAM);
                }
                showContentView();

                if(mAlbumBean!=null){
                        setTitle(mAlbumBean.getName());
                        bindingView.setBean(mAlbumBean);
                        initUi();
                }

        }


        private void initUi(){
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = BookRankListFragment.newInstance(Constants.FILTER,mAlbumBean.getFilter());
                fragmentTransaction.add(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.activity_search, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();
                if (id == R.id.search) {
                        Intent intent = new Intent(AlbumDetailActivity.this, BookSearchActivity.class);
                        startActivity(intent);
                }

                return super.onOptionsItemSelected(item);
        }

        public static void start(Context mContext,AlbumBean albumBean) {
                Intent intent = new Intent(mContext, AlbumDetailActivity.class);
                intent.putExtra(EXTRA_PARAM,albumBean);
                mContext.startActivity(intent);
        }
        @Override
        public void onResume() {
                super.onResume();
        }

        @Override
        public void onPause() {
                super.onPause();
        }
}
