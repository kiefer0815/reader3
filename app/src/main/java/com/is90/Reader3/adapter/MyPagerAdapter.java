package com.is90.Reader3.adapter;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.is90.Reader3.fragment.BookMarkFragment;
import com.is90.Reader3.fragment.CatalogFragment;

/**
 * Created by Administrator on 2016/1/12.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private CatalogFragment catalogueFragment;
    private BookMarkFragment bookMarkFragment;
    private String bookPath;
    private final String[] titles = { "目录", "书签" };

    public MyPagerAdapter(FragmentManager fm,String bookPath) {
        super(fm);
        this.bookPath = bookPath;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (catalogueFragment == null) {
                    //  bookMarkFragment = new BookMarkFragment();
                    //创建bookMarkFragment实例时同时把需要intent中的值传入
//                    catalogueFragment = CatalogFragment
                   // bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                    catalogueFragment = CatalogFragment.newInstance(bookPath);
                }
                return catalogueFragment;

            case 1:
                if (bookMarkFragment == null) {
                    //catalogueFragment = new CatalogueFragment();
                  //  catalogueFragment = CatalogueFragment.newInstance(MarkActivity.getBookpath_intent());
//                    bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                    bookMarkFragment = BookMarkFragment.newInstance(bookPath);
                }
                return bookMarkFragment;
        }

        return null;
    }

}
