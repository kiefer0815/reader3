package com.is90.Reader3.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.pingplusplus.android.Pingpp;
import com.is90.Reader3.MainActivity;
import com.is90.Reader3.R;
import com.is90.Reader3.UserManager;
import com.is90.Reader3.bean.MoviesBean;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.component.rx.RxBus;
import com.is90.Reader3.component.rx.RxBusBaseMessage;
import com.is90.Reader3.component.rx.RxCodeConstants;
import com.is90.Reader3.databinding.FooterItemBookBinding;
import com.is90.Reader3.databinding.HeaderItemBookBinding;
import com.is90.Reader3.databinding.ItemMovieBinding;
import com.is90.Reader3.exception.TokenExpiredException;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.PerfectClickListener;
import com.is90.Reader3.utils.UIUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiefer on 2017/6/28.
 */


public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private MainActivity context;

        private int status = 1;
        public static final int LOAD_MORE = 0;
        public static final int LOAD_PULL_TO = 1;
        public static final int LOAD_NONE = 2;
        private static final int LOAD_END = 3;
        private static final int TYPE_TOP = -1;

        private static final int TYPE_FOOTER_BOOK = -2;
        private static final int TYPE_HEADER_BOOK = -3;
        private static final int TYPE_CONTENT_BOOK = -4;
        private List<MoviesBean> list;

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = MovieAdapter.class.getSimpleName();

        public MovieAdapter(Context context) {
                this.context = (MainActivity) context;
                list = new ArrayList<>();

        }

        @Override
        public int getItemViewType(int position) {

                if (position == 0) {
                        return TYPE_HEADER_BOOK;
                } else if (position + 1 == getItemCount()) {
                        return TYPE_FOOTER_BOOK;
                } else {
                        return TYPE_CONTENT_BOOK ;
                }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                case TYPE_HEADER_BOOK:
                        HeaderItemBookBinding mBindHeader = DataBindingUtil
                                .inflate(LayoutInflater.from(parent.getContext()), R.layout.header_item_book, parent, false);
                        return new HeaderViewHolder(mBindHeader.getRoot());
                case TYPE_FOOTER_BOOK:
                        FooterItemBookBinding mBindFooter = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.footer_item_book, parent, false);
                        return new FooterViewHolder(mBindFooter.getRoot());
                default:
                        ItemMovieBinding mBindBook = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_movie, parent, false);
                        return new MovieViewHolder(mBindBook.getRoot());
                }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof HeaderViewHolder) {
                        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                        headerViewHolder.bindItem();
                } else if (holder instanceof FooterViewHolder) {
                        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                        footerViewHolder.bindItem();
                } else if (holder instanceof MovieViewHolder) {
                        MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
                        if (list != null && list.size() > 0) {
                                // 内容从"1"开始
                                //                DebugUtil.error("------position: "+position);
                                movieViewHolder.bindItem(list.get(position - 1), position-1);
                        }
                }
        }

        @Override
        public int getItemCount() {
                return list.size() + 2;
        }

        /**
         * 处理 GridLayoutManager 添加头尾布局占满屏幕宽的情况
         */
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                if (manager instanceof GridLayoutManager) {
                        final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                        gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                        return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                                }
                        });
                }
        }

        /**
         * 处理 StaggeredGridLayoutManager 添加头尾布局占满屏幕宽的情况
         */
        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
                if (lp != null
                        && lp instanceof StaggeredGridLayoutManager.LayoutParams
                        && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                        StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                        p.setFullSpan(true);
                }
        }

        /**
         * 这里规定 position = 0 时
         * 就为头布局，设置为占满整屏幕宽
         */
        private boolean isHeader(int position) {
                return position >= 0 && position < 1;
        }

        /**
         * 这里规定 position =  getItemCount() - 1时
         * 就为尾布局，设置为占满整屏幕宽
         * getItemCount() 改了 ，这里就不用改
         */
        private boolean isFooter(int position) {
                return position < getItemCount() && position >= getItemCount() - 1;
        }

        /**
         * footer view
         */
        private class FooterViewHolder extends RecyclerView.ViewHolder {

                FooterItemBookBinding mBindFooter;

                FooterViewHolder(View itemView) {
                        super(itemView);
                        mBindFooter = DataBindingUtil.getBinding(itemView);
                        mBindFooter.rlMore.setGravity(Gravity.CENTER);
                        //            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(context, 40));
                        //            itemView.setLayoutParams(params);
                }

                private void bindItem() {
                        switch (status) {
                        case LOAD_MORE:
                                mBindFooter.progress.setVisibility(View.VISIBLE);
                                mBindFooter.tvLoadPrompt.setText("正在加载...");
                                itemView.setVisibility(View.VISIBLE);
                                break;
                        case LOAD_PULL_TO:
                                mBindFooter.progress.setVisibility(View.GONE);
                                mBindFooter.tvLoadPrompt.setText("点击刷新换一批");
                                itemView.setVisibility(View.VISIBLE);
                                break;
                        case LOAD_NONE:
                                System.out.println("LOAD_NONE----");
                                mBindFooter.progress.setVisibility(View.GONE);
                                mBindFooter.tvLoadPrompt.setText("没有更多内容了");
                                break;
                        case LOAD_END:
                                itemView.setVisibility(View.GONE);
                        default:
                                break;
                        }
                }
        }


        private class HeaderViewHolder extends RecyclerView.ViewHolder {

                HeaderItemBookBinding mBindBook;

                HeaderViewHolder(View view) {
                        super(view);
                        mBindBook = DataBindingUtil.getBinding(view);
                }

                private void bindItem() {
                        //            mBindBook.setBean(book);
                        //            mBindBook.executePendingBindings();
                }
        }

        public void updateLoadStatus(int status) {
                this.status = status;
                notifyDataSetChanged();
        }

        public int getLoadStatus(){
                return this.status;
        }


        private class MovieViewHolder extends RecyclerView.ViewHolder {

                ItemMovieBinding mBindMovie;

                MovieViewHolder(View view) {
                        super(view);
                        mBindMovie = DataBindingUtil.getBinding(view);
                }

                private void bindItem(final MoviesBean moviesBean, int position) {
                        mBindMovie.setBean(moviesBean);
                        mBindMovie.executePendingBindings();

                        mBindMovie.llItemTop.setOnClickListener(new PerfectClickListener() {
                                @Override
                                protected void onNoDoubleClick(View v) {
                                        if(moviesBean.getMovie_price().equals("0.0")){
//                                                String url = Constants.GIF_PRE_UTL + moviesBean.getMovie_file_id();
//                                                FileDownloadManager.uniqueInstance().startDownloadMovieFile(moviesBean.getMovie_name(),url);
                                        }else {
                                                if(UserManager.uniqueInstance().getUserId()==-1){
                                                        RxBus.getDefault().post(RxCodeConstants.SHOW_LOGIN, new RxBusBaseMessage());
                                                }else {
                                                        Map<String ,String> map = new HashMap<>();
                                                        map.put("channel","wx");// alipay	支付宝 APP 支付 wx	微信 APP 支付
                                                        map.put("bookId",moviesBean.getId()+"");
                                                        map.put("price",moviesBean.getMovie_price());
                                                        map.put("type","movie");

                                                        RestClientFactory.createApi()
                                                                .createOrder(map)
                                                                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                                                                .unsubscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new BaseSubscriber<Object>() {
                                                                        @Override
                                                                        public void onSuccess(Object data) {
                                                                                String charge = JSON.toJSONString(data);
                                                                                Pingpp.createPayment(context, charge);
                                                                                RxBus.getDefault().post(RxCodeConstants.WAITING_MOVIE_FOR_PAY, moviesBean);
                                                                        }

                                                                        @Override
                                                                        public void onError(Throwable e) {
                                                                                super.onError(e);
                                                                                if(e instanceof TokenExpiredException){
                                                                                        UIUtils.makeToast(context,"请重新登陆");
                                                                                        UserManager.uniqueInstance().handleLoginOut();
                                                                                        RxBus.getDefault().post(RxCodeConstants.SHOW_LOGOUT, new RxBusBaseMessage());
                                                                                }
                                                                                logger.d(TAG,e.getMessage());
                                                                        }

                                                                        @Override
                                                                        public void onFinally(Throwable e) {
                                                                                super.onFinally(e);
                                                                        }
                                                                });
                                                }
                                        }


                                }
                        });


                }
        }
        public List<MoviesBean> getList() {
                return list;
        }

        public void setList(List<MoviesBean> list) {
                this.list.clear();
                this.list = list;
        }

        public void addAll(List<MoviesBean> list) {
                this.list.addAll(list);
        }

}
