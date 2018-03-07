//package com.is90.Reader3.fragment;
//
///**
// * Created by kiefer on 2017/6/28.
// */
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import com.is90.Reader3.MainActivity;
//import com.is90.Reader3.R;
//import com.is90.Reader3.adapter.BookAdapter;
//import com.is90.Reader3.adapter.MovieAdapter;
//import com.is90.Reader3.base.BaseDataBindingFragment;
//import com.is90.Reader3.bean.MoviesBean;
//import com.is90.Reader3.bean.MoviesList;
//import com.is90.Reader3.component.log.RsLogger;
//import com.is90.Reader3.component.log.RsLoggerManager;
//import com.is90.Reader3.component.rx.RxBus;
//import com.is90.Reader3.component.rx.RxBusBaseMessage;
//import com.is90.Reader3.component.rx.RxCodeConstants;
//import com.is90.Reader3.databinding.FragmentMovieCustomBinding;
//import com.is90.Reader3.network.BaseSubscriber;
//import com.is90.Reader3.network.RestClientFactory;
//import com.is90.Reader3.utils.CommonUtils;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//
//
//public class MovieCustomFragment extends BaseDataBindingFragment<FragmentMovieCustomBinding> {
//
//        private boolean mIsPrepared;
//        private boolean mIsFirst = true;
//        // 开始请求的角标
//        private MainActivity activity;
//        private MovieAdapter mMovieAdapter;
//        private GridLayoutManager mLayoutManager;
//        RsLogger logger = RsLoggerManager.getLogger();
//        public static final String TAG = MovieCustomFragment.class.getSimpleName();
//        private List<List<MoviesBean>> totalList = new ArrayList<List<MoviesBean>>();
//        private int currentPage = 0;
//
//        @Override
//        public int setContent() {
//                return R.layout.fragment_movie_custom;
//        }
//
//        @Override
//        public void onAttach(Context context) {
//                super.onAttach(context);
//                activity = (MainActivity) context;
//        }
//
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//
//        }
//
//        @Override
//        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//                super.onActivityCreated(savedInstanceState);
//                showContentView();
//                bindingView.srlMovie.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
//                bindingView.srlMovie.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                        @Override
//                        public void onRefresh() {
//                                //                listTag= Arrays.asList(BookApiUtils.getApiTag(position));
//                                //                String tag=BookApiUtils.getRandomTAG(listTag);
//                                //                doubanBookPresenter.searchBookByTag(BookReadingFragment.this,tag,false);
//                                refreshList();
//
//
//                        }
//                });
//
//                //        mBookAdapter = new BookAdapter(getActivity());
//
//                //        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//
//                mLayoutManager = new GridLayoutManager(getActivity(), 2);
//                bindingView.xrvMovie.setLayoutManager(mLayoutManager);
//
//                //        bindingView.xrvBook.setAdapter(mBookAdapter);
//
//                scrollRecycleView();
//
//                // 准备就绪
//                mIsPrepared = true;
//                /**
//                 * 因为启动时先走loadData()再走onActivityCreated，
//                 * 所以此处要额外调用load(),不然最初不会加载内容
//                 */
//                loadData();
//                RxBus.getDefault().toObservable(RxCodeConstants.REFRESH_MOVIE_LIST, RxBusBaseMessage.class)
//                        .subscribe(new Action1<RxBusBaseMessage>() {
//                                @Override
//                                public void call(RxBusBaseMessage integer) {
//                                        refreshList();
//                                }
//                        });
//        }
//
//
//        private void refreshList(){
//                currentPage = currentPage+1;
//                if(currentPage < totalList.size()){
//                        mMovieAdapter = new MovieAdapter(getActivity());
//                        mMovieAdapter.setList(totalList.get(currentPage));
//                        mMovieAdapter.notifyDataSetChanged();
//                        bindingView.xrvMovie.setAdapter(mMovieAdapter);
//                }else{
//                        currentPage = 0;
//                        mMovieAdapter = new MovieAdapter(getActivity());
//                        mMovieAdapter.setList(totalList.get(0));
//                        mMovieAdapter.notifyDataSetChanged();
//                        bindingView.xrvMovie.setAdapter(mMovieAdapter);
//                }
//                bindingView.srlMovie.setRefreshing(false);
//        }
//
//        @Override
//        protected void loadData() {
//                RsLoggerManager.getLogger().e("Book Custom", "-----loadData");
//                if (!mIsPrepared || !mIsVisible || !mIsFirst) {
//                        return;
//                }
//
//                bindingView.srlMovie.setRefreshing(true);
//                bindingView.srlMovie.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                                loadCustomData();
//                        }
//                }, 500);
//                RsLoggerManager.getLogger().e("Book Custom", "-----setRefreshing");
//        }
//
//        private void loadCustomData() {
//                addSubscription(RestClientFactory.createApi()
//                        .getMovieList()
//                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new BaseSubscriber<MoviesList>() {
//                                @Override
//                                public void onSuccess(MoviesList listBean) {
//                                        if (listBean !=null && listBean.getList().size() > 0) {
//                                                if (mMovieAdapter == null) {
//                                                        mMovieAdapter = new MovieAdapter(getActivity());
//                                                }
//                                                totalList = CommonUtils.spliceArrays(listBean.getList(),25);
//                                                if(totalList.size()>0){
//                                                        mMovieAdapter.setList(totalList.get(0));
//                                                        mMovieAdapter.notifyDataSetChanged();
//                                                        bindingView.xrvMovie.setAdapter(mMovieAdapter);
//                                                }
//
//
//                                        }
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                        super.onError(e);
//                                        logger.e(TAG, e.getMessage());
//                                }
//
//                                @Override
//                                public void onFinally(Throwable e) {
//                                        super.onFinally(e);
//                                        bindingView.srlMovie.setRefreshing(false);
//                                        mIsFirst = false;
//                                }
//                        }));
//        }
//
//        public void scrollRecycleView() {
//                bindingView.xrvMovie.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                        int lastVisibleItem;
//
//                        @Override
//                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                                super.onScrollStateChanged(recyclerView, newState);
//                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                                        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//
//                                        /**StaggeredGridLayoutManager*/
//                                        //                    int[] into = new int[(mLayoutManager).getSpanCount()];
//                                        //                    lastVisibleItem = findMax(mLayoutManager.findLastVisibleItemPositions(into));
//
//                                        if (mMovieAdapter == null) {
//                                                return;
//                                        }
//                                        if (mLayoutManager.getItemCount() == 0) {
//                                                mMovieAdapter.updateLoadStatus(BookAdapter.LOAD_NONE);
//                                                return;
//
//                                        }
//                                        if (lastVisibleItem + 1 == mLayoutManager.getItemCount()
//                                                && mMovieAdapter.getLoadStatus() != BookAdapter.LOAD_MORE) {
//
//                                                //                                                mBookAdapter.updateLoadStatus(BookAdapter.LOAD_MORE);
//                                                //
//                                                //                                                new Handler().postDelayed(new Runnable() {
//                                                //                                                        @Override
//                                                //                                                        public void run() {
//                                                //                                                                //                                String tag= BookApiUtils.getRandomTAG(listTag);
//                                                //                                                                //                                doubanBookPresenter.searchBookByTag(BookReadingFragment.this,tag,true);
//                                                //                                                                mStart += mCount;
//                                                //                                                                loadCustomData();
//                                                //                                                        }
//                                                //                                                }, 1000);
//                                        }
//                                }
//                        }
//
//                        @Override
//                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                                super.onScrolled(recyclerView, dx, dy);
//                                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//
//                        }
//                });
//        }
//
//
//        @Override
//        protected void onRefresh() {
//                bindingView.srlMovie.setRefreshing(true);
//                refreshList();
//        }
//}
//
