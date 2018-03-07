package com.is90.Reader3.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.ReadActivity;
import com.is90.Reader3.base.BaseHeaderActivity;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.NovelsBean;
import com.is90.Reader3.component.db.BookList;
import com.is90.Reader3.databinding.ActivityBookDetailBinding;
import com.is90.Reader3.databinding.HeaderBookDetailBinding;
import com.is90.Reader3.manager.FileDownloadManager;
import com.is90.Reader3.manager.PreferenceManager;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.CommonUtils;
import com.is90.Reader3.utils.UIUtils;
import com.uhmtech.util.LZMA;
import com.xiaochen.progressroundbutton.AnimDownloadProgressButton;
import org.litepal.crud.DataSupport;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.List;

public class BookDetailActivity extends BaseHeaderActivity<HeaderBookDetailBinding, ActivityBookDetailBinding>
        implements NativeExpressAD.NativeExpressADListener {

        private NovelsBean novelsBean;
        private String mBookDetailUrl;
        private String mBookDetailName;
        public final static String EXTRA_PARAM = "bookBean";
        public static final String TAG = BookDetailActivity.class.getSimpleName();
        private NativeExpressAD nativeExpressAD;
        private ViewGroup container;
        private NativeExpressADView nativeExpressADView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_book_detail);

                if (getIntent() != null) {
                        novelsBean = (NovelsBean) getIntent().getSerializableExtra(EXTRA_PARAM);
                }

                setMotion(setHeaderPicView(), true);
                initSlideShapeTheme(setHeaderImgUrl(), setHeaderImageView());

                setTitle(novelsBean.getBook_name());
                setSubTitle("作者：" + novelsBean.getAuthor());
                bindingHeaderView.setNovel(novelsBean);
                bindingHeaderView.executePendingBindings();
                bindingContentView.setNovel(novelsBean);
                bindingContentView.executePendingBindings();
                if (checkBook()) {
                        bindingContentView.animBtn.setCurrentText("打开");
                        bindingContentView.animBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        final List<BookList> books = DataSupport
                                                .where("bookname = ?", novelsBean.getBook_name()).find(BookList.class);
                                        ReadActivity.openBook(books.get(0), BookDetailActivity.this);
                                }
                        });
                } else {
                        bindingContentView.animBtn.setCurrentText("下载");
                        bindingContentView.animBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                        if (checkBook()) {
                                                UIUtils.makeToast(BookDetailActivity.this, "已经开始下载了，请稍后");
                                        } else {
                                                updateDownloadCount();
                                                startDownloadFile(novelsBean.getBook_name(),
                                                        novelsBean.getBook_sha1_file());
                                        }
                                }
                        });
                }

                showContentView();

                container = bindingContentView.container;
                final float density = getResources().getDisplayMetrics().density;
                ADSize adSize = new ADSize((int) (getResources().getDisplayMetrics().widthPixels / density), 130); // 不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
                nativeExpressAD = new NativeExpressAD(this, adSize, Constants.APPID, Constants.NativeExpressPosID2, this);
                nativeExpressAD.loadAD(1);
        }

        private void updateDownloadCount() {
                addSubscription(RestClientFactory.createApi()
                        .updateDownloadCount(novelsBean.getId())
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<Object>() {
                                @Override
                                public void onSuccess(Object o) {
                                }

                                @Override
                                public void onError(Throwable e) {
                                        super.onError(e);
                                }

                                @Override
                                public void onFinally(Throwable e) {
                                        super.onFinally(e);
                                }
                        }));
        }

        private boolean checkBook() {
                final List<BookList> books = DataSupport.where("bookname = ?", novelsBean.getBook_name())
                        .find(BookList.class);
                return books.size() > 0;
        }

        @Override
        protected int setHeaderLayout() {
                return R.layout.header_book_detail;
        }

        @Override
        protected void setTitleClickMore() {
                //        WebViewActivity.loadUrl(this, mBookDetailUrl, mBookDetailName);
        }

        @Override
        protected String setHeaderImgUrl() {
                if (novelsBean == null) {
                        return "";
                }
                return PreferenceManager.uniqueInstance().getBookCoverUrl() + novelsBean.getBook_sha1_file();
        }

        @Override
        protected ImageView setHeaderImageView() {
                return bindingHeaderView.imgItemBg;
        }

        @Override
        protected ImageView setHeaderPicView() {
                return bindingHeaderView.ivOnePhoto;
        }

        @Override
        protected void onRefresh() {
        }

        /**
         * @param context      activity
         * @param positionData bean
         * @param imageView    imageView
         */
        public static void start(Activity context, NovelsBean positionData, ImageView imageView) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra(EXTRA_PARAM, positionData);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                                imageView, CommonUtils.getString(R.string.transition_book_img));//与xml文件对应
                ActivityCompat.startActivity(context, intent, options.toBundle());
        }

        public static void start(Context mContext, NovelsBean positionData) {
                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtra(EXTRA_PARAM, positionData);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
        }

        public void startDownloadFile(final String task_name, final String sha1_file) {
                FileDownloadManager.checkPath(FileDownloadManager.path);
                final String download_path = FileDownloadManager.path + sha1_file + ".7z";
                final String out_path = FileDownloadManager.path + task_name + ".txt";
                final BookList bookList = new BookList();
                bookList.setBookname(task_name);
                bookList.setBookpath(out_path);
                bookList.setStatus("1");
                bookList.save();
                bindingContentView.animBtn.setCurrentText("开始启动下载");
                FileDownloader.getImpl().create(
                        PreferenceManager.uniqueInstance().getBookUrl() + sha1_file)
                        .setPath(download_path)
                        .setListener(new FileDownloadListener() {
                                @Override
                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                        int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                        float num = (float) soFarBytes / totalBytes;
                                        bindingContentView.animBtn.setState(AnimDownloadProgressButton.DOWNLOADING);
                                        bindingContentView.animBtn.setProgressText("下载中", (num * 100));
                                }

                                @Override
                                protected void blockComplete(BaseDownloadTask task) {
                                }

                                @Override
                                protected void retry(final BaseDownloadTask task, final Throwable ex,
                                        final int retryingTimes, final int soFarBytes) {
                                }

                                @Override
                                protected void completed(BaseDownloadTask task) {
                                        extractFile(download_path, task_name, sha1_file, bookList);
                                }

                                @Override
                                protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                }

                                @Override
                                protected void error(BaseDownloadTask task, Throwable e) {
                                        DataSupport.deleteAll(BookList.class, "bookname=?", task_name);
                                        bindingContentView.animBtn.setState(AnimDownloadProgressButton.NORMAL);
                                        bindingContentView.animBtn.setCurrentText("下载");
                                        UIUtils.makeToast(BookDetailActivity.this, "下载失败请重试");
                                }

                                @Override
                                protected void warn(BaseDownloadTask task) {
                                }
                        }).start();
        }

        @Override
        public void onResume() {
                super.onResume();
        }

        @Override
        public void onPause() {
                super.onPause();
        }

        private void extractFile(final String filePath, final String task_name, final String sha1_file,
                final BookList bookList) {
                new AsyncTask<Void, Void, Void>() {
                        private int success = 1;
                        private File oriFile = null;
                        private File readFile = null;
                        private File zipFile = null;

                        protected void onPreExecute() {
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                                zipFile = new File(filePath);
                                success = LZMA.extract7z(filePath, FileDownloadManager.path);
                                oriFile = new File(FileDownloadManager.path + sha1_file);
                                readFile = new File(FileDownloadManager.path + task_name + ".txt");
                                oriFile.renameTo(readFile);
                                return null;
                        }

                        protected void onPostExecute(Void result) {
                                if (zipFile != null)
                                        zipFile.delete();
                                if (success == 0 && readFile != null) {
                                        bookList.setStatus("0");
                                        bookList.updateAll("bookname=?", task_name);

                                        bindingContentView.animBtn.setState(AnimDownloadProgressButton.NORMAL);
                                        bindingContentView.animBtn.setCurrentText("打开");
                                        bindingContentView.animBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                        ReadActivity.openBook(bookList, BookDetailActivity.this);
                                                }
                                        });
                                } else {
                                        DataSupport.deleteAll(BookList.class, "bookname=?", task_name);
                                        if (oriFile != null)
                                                oriFile.delete();
                                        if (readFile != null)
                                                readFile.delete();
                                        bindingContentView.animBtn.setState(AnimDownloadProgressButton.NORMAL);
                                        bindingContentView.animBtn.setCurrentText("下载");
                                        UIUtils.makeToast(BookDetailActivity.this, "下载失败请重试");
                                }

                        }
                }.execute();
        }

        //广告相关
        @Override
        public void onNoAD(AdError adError) {
                Log.i(
                        TAG,
                        String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
                                adError.getErrorMsg()));
        }

        @Override
        public void onADLoaded(List<NativeExpressADView> adList) {
                Log.i(TAG, "onADLoaded: " + adList.size());
                // 释放前一个NativeExpressADView的资源
                if (nativeExpressADView != null) {
                        nativeExpressADView.destroy();
                }

                if (container.getVisibility() != View.VISIBLE) {
                        container.setVisibility(View.VISIBLE);
                }

                if (container.getChildCount() > 0) {
                        container.removeAllViews();
                }

                nativeExpressADView = adList.get(0);
                // 保证View被绘制的时候是可见的，否则将无法产生曝光和收益。
                container.addView(nativeExpressADView);
                nativeExpressADView.render();
        }

        @Override
        public void onRenderFail(NativeExpressADView adView) {
                Log.i(TAG, "onRenderFail");
        }

        @Override
        public void onRenderSuccess(NativeExpressADView adView) {
                Log.i(TAG, "onRenderSuccess");
        }

        @Override
        public void onADExposure(NativeExpressADView adView) {
                Log.i(TAG, "onADExposure");
        }

        @Override
        public void onADClicked(NativeExpressADView adView) {
                Log.i(TAG, "onADClicked");
        }

        @Override
        public void onADClosed(NativeExpressADView adView) {
                Log.i(TAG, "onADClosed");
                // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，不再可用。
                if (container != null && container.getChildCount() > 0) {
                        container.removeAllViews();
                        container.setVisibility(View.GONE);
                }
        }

        @Override
        public void onADLeftApplication(NativeExpressADView adView) {
                Log.i(TAG, "onADLeftApplication");
        }

        @Override
        public void onADOpenOverlay(NativeExpressADView adView) {
                Log.i(TAG, "onADOpenOverlay");
        }

        @Override
        public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

        }

}
