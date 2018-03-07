package com.is90.Reader3.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.is90.Reader3.R;
import com.is90.Reader3.bean.GankIoDataBean;
import com.is90.Reader3.bean.NovelsBean;
import com.is90.Reader3.utils.DensityUtil;
import com.is90.Reader3.utils.ImageUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jingbin on 2016/12/1.
 */

public class WelfareAdapter extends BaseAdapter<Object> {
    static final int TYPE_DATA = 0;
    static final int TYPE_AD = 1;
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = (viewType == TYPE_AD) ? R.layout.item_welfare_ad : R.layout.item_welfare;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent,false);;
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public WelfareAdapter(Context context, List<Object> list,HashMap<NativeExpressADView, Integer> map) {
        super(context, list);
        mAdViewPositionMap =  map;
    }

    class ViewHolder extends BaseViewHolder {

        public ImageView bookCover;
        public FrameLayout container;

        ViewHolder(View view) {
            super(view);
            bookCover = (ImageView) view.findViewById(R.id.iv_welfare);
            container = (FrameLayout) view.findViewById(R.id.express_ad_container);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int type = getItemViewType(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        if (TYPE_AD == type) {
            final NativeExpressADView adView = (NativeExpressADView) mDataSet.get(position);
            mAdViewPositionMap.put(adView, position); // 广告在列表中的位置是可以被更新的
            if (viewHolder.container.getChildCount() > 0
                    && viewHolder.container.getChildAt(0) == adView) {
                return;
            }

            if (viewHolder.container.getChildCount() > 0) {
                viewHolder.container.removeAllViews();
            }

            if (adView.getParent() != null) {
                ((ViewGroup) adView.getParent()).removeView(adView);
            }

            viewHolder.container.addView(adView);
            adView.render(); // 调用render方法后sdk才会开始展示广告
        } else{
            final GankIoDataBean.ResultBean novelsBean = (GankIoDataBean.ResultBean) mDataSet.get(position);
            ImageUtil.displayFadeImage(viewHolder.bookCover,novelsBean.getUrl(),1);

            if (position % 2 == 0) {
                DensityUtil.setViewMargin(viewHolder.bookCover, false, 12, 6, 12, 0);
            } else {
                DensityUtil.setViewMargin(viewHolder.bookCover, false, 6, 12, 12, 0);
            }
        }

    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = parent.getWidth() / 90;
                outRect.set(0, 0, 0, offset);
            }
        };
    }

    public void removeItemById(long id) {
        for (Object book : mDataSet) {
            if (book instanceof NovelsBean && id == ((NovelsBean)book).getId()) {
                remove(book);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) instanceof NativeExpressADView ? TYPE_AD : TYPE_DATA;
    }
    // 把返回的NativeExpressADView添加到数据集里面去
    public void addADViewToPosition(int position, NativeExpressADView adView) {
        if (position >= 0 && position < mDataSet.size() && adView != null) {
            mDataSet.add(position, adView);
            notifyItemInserted(mDataSet.size());
        }
    }

    // 移除NativeExpressADView的时候是一条一条移除的
    public void removeADView(int position, NativeExpressADView adView) {
        mDataSet.remove(position);
        notifyItemRemoved(position); // position为adView在当前列表中的位置
        notifyItemRangeChanged(0, mDataSet.size() - 1);
    }
}
