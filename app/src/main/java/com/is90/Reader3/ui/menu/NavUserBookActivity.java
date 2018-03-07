package com.is90.Reader3.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.is90.Reader3.R;
import com.is90.Reader3.base.BaseDataBindingActivity;
import com.is90.Reader3.component.filechooser.FileChooserActivity;
import com.is90.Reader3.databinding.ActivityUserBookBinding;
import com.is90.Reader3.fragment.LocalBookListFragment;

/**
 * Created by kiefer on 2017/6/23.
 */

public class NavUserBookActivity extends BaseDataBindingActivity<ActivityUserBookBinding> {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_user_book);
                showContentView();

                setTitle("我的小说");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LocalBookListFragment()).commit();
        }

        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, NavUserBookActivity.class);
                mContext.startActivity(intent);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.user_book, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                if (id == R.id.import_book) {
                        Intent intent = new Intent(NavUserBookActivity.this, FileChooserActivity.class);
                        startActivity(intent);
                }

                return super.onOptionsItemSelected(item);
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