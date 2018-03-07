package com.is90.Reader3.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;
import com.is90.Reader3.ReadActivity;
import com.is90.Reader3.UserManager;
import com.is90.Reader3.adapter.ShelfAdapter;
import com.is90.Reader3.base.BaseFragment;
import com.is90.Reader3.component.db.BookList;
import com.is90.Reader3.component.rx.RxBus;
import com.is90.Reader3.component.rx.RxBusBaseMessage;
import com.is90.Reader3.component.rx.RxCodeConstants;
import com.is90.Reader3.view.DragGridView;
import com.is90.Reader3.R;
import org.litepal.crud.DataSupport;
import rx.functions.Action1;

import java.io.File;
import java.util.List;

/**
 * Created by kiefer on 2017/6/14.
 */

public class LocalBookListFragment extends BaseFragment{

        @Bind(R.id.bookShelf)
        DragGridView bookShelf;
        private List<BookList> bookLists;
        private ShelfAdapter adapter;
        //点击书本的位置
        private int itemPosition;

        @Override
        protected int getLayoutRes() {
                return R.layout.fragment_localbooklist;
        }

        @Override
        protected void initData(View view) {
                bookLists = DataSupport.findAll(BookList.class);
                adapter = new ShelfAdapter(getActivity(),bookLists);
                bookShelf.setAdapter(adapter);
                RxBus.getDefault().toObservable(RxCodeConstants.REFRESH_LOCAL_BOOK_LIST, RxBusBaseMessage.class)
                        .subscribe(new Action1<RxBusBaseMessage>() {
                                @Override
                                public void call(RxBusBaseMessage integer) {
                                        DragGridView.setIsShowDeleteButton(false);
                                        bookLists = DataSupport.findAll(BookList.class);
                                        adapter.setBookList(bookLists);
                                }
                        });
        }

        @Override
        protected void initListener() {
                bookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (bookLists.size() > position) {
                                        itemPosition = position;
                                        String bookname = bookLists.get(itemPosition).getBookname();

                                        adapter.setItemToFirst(itemPosition);
                                        //                bookLists = DataSupport.findAll(BookList.class);
                                        final BookList bookList = bookLists.get(itemPosition);
                                        bookList.setId(bookLists.get(0).getId());
                                        final String path = bookList.getBookpath();
                                        File file = new File(path);
                                        if (!file.exists()){
                                                new AlertDialog.Builder(getActivity())
                                                        .setTitle(getActivity().getString(R.string.app_name))
                                                        .setMessage(path + "文件不存在,是否删除该书本？")
                                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                        DataSupport.deleteAll(BookList.class, "bookpath = ?", path);
                                                                        bookLists = DataSupport.findAll(BookList.class);
                                                                        adapter.setBookList(bookLists);
                                                                }
                                                        }).setCancelable(true).show();
                                                return;
                                        }

                                        ReadActivity.openBook(bookList,getActivity());
                                }
                        }
                });
        }

        @Override
        public void onResume() {
                super.onResume();
                DragGridView.setIsShowDeleteButton(false);
                bookLists = DataSupport.findAll(BookList.class);
                adapter.setBookList(bookLists);

        }

        @Override
        protected void onVisible() {
                super.onVisible();
                DragGridView.setIsShowDeleteButton(false);
                bookLists = DataSupport.findAll(BookList.class);
                adapter.setBookList(bookLists);
        }
}
