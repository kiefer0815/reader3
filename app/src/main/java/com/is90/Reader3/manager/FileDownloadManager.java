package com.is90.Reader3.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.is90.Reader3.AppContext;
import com.is90.Reader3.component.db.BookList;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;

import java.io.File;

/**
 * Created by kiefer on 2017/6/17.
 */

public class FileDownloadManager {
        public static String path =  AppContext.applicationContext.getFilesDir() + File.separator + "download" + File.separator ;
        private static FileDownloadManager fileDownloadManager;
        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = FileDownloadManager.class.getSimpleName();
        private Context mContext;
        public void init(Context context){
                mContext = context;
        }
        ProgressDialog dialog;
        private int progress = 0;

        public static FileDownloadManager uniqueInstance() {
                FileDownloadManager result = fileDownloadManager;
                if (result == null) {
                        synchronized (FileDownloadManager.class) {
                                result = fileDownloadManager;
                                if (result == null)
                                        result = fileDownloadManager = new FileDownloadManager();

                        }
                }

                return result;
        }

        public void startDownloadFile(final String task_name,String url){
               checkPath(path);
                String download_path = path+task_name+".txt";
                final BookList bookList = new BookList();
                bookList.setBookname(task_name);
                bookList.setBookpath(download_path);
                bookList.setStatus("1");
                bookList.save();
                Toast.makeText( AppContext.applicationContext, task_name + "开始下载", Toast.LENGTH_SHORT).show();
                FileDownloader.getImpl().create(url)
                        .setPath(download_path)
                        .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void blockComplete(BaseDownloadTask task) {
                                }

                                @Override
                                protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                        bookList.setStatus("0");
                                        bookList.updateAll("bookname=?",task_name);
                                        Toast.makeText( AppContext.applicationContext, task_name + "下载完成", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {
                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {
                                }
                        }).start();
        }

        public static void checkPath(String path) {
                File file = new File(path);
                if (!file.exists())
                        file.mkdirs();
        }


}
