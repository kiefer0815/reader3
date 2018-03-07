package com.is90.Reader3.manager;

import android.os.AsyncTask;
import android.util.Log;
import com.is90.Reader3.UserManager;
import com.is90.Reader3.component.db.BookList;
import org.litepal.exceptions.DataSupportException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiefer on 2017/6/17.
 */

public class BookTaskManager {
        protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<>();
        public void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
                myAsyncTasks.add(asyncTask.execute());
        }

        private static BookTaskManager bookTaskManager;
        public static BookTaskManager uniqueInstance() {
                BookTaskManager result = bookTaskManager;
                if (result == null) {
                        synchronized (BookTaskManager.class) {
                                result = bookTaskManager;
                                if (result == null)
                                        result = bookTaskManager = new BookTaskManager();

                        }
                }

                return result;
        }

        /**
         * 数据库书本信息更新
         * @param databaseId  要更新的数据库的书本ID
         * @param bookList
         */
        public void upDateBookToSqlite3(final int databaseId,final BookList bookList) {

                putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected void onPreExecute() {

                        }

                        @Override
                        protected Boolean doInBackground(Void... params) {
                                try {
                                        bookList.update(databaseId);
                                } catch (DataSupportException e) {
                                        return false;
                                }
                                return true;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                                if (result) {

                                } else {
                                        Log.d("保存到数据库结果-->", "失败");
                                }
                        }
                });
        }

}
