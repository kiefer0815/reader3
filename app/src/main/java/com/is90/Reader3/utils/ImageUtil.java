package com.is90.Reader3.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.is90.Reader3.R;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.manager.PreferenceManager;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by kiefer on 2017/6/16.
 */

public class ImageUtil {
        /**
         * 妹子，电影列表图
         *
         * @param defaultPicType 电影：0；妹子：1； 书籍：2
         */
        @BindingAdapter({"android:displayFadeImage", "android:defaultPicType"})
        public static void displayFadeImage(ImageView imageView, String url, int defaultPicType) {
                displayEspImage(url, imageView, defaultPicType);
        }
        /**
         * 书籍、妹子图、电影列表图
         * 默认图区别
         */
        public static void displayEspImage(String url, ImageView imageView, int type) {
                Glide.with(imageView.getContext())
                        .load(url)
                        .crossFade(500)
                        .placeholder(getDefaultPic(type))
                        .error(getDefaultPic(type))
                        .into(imageView);
        }
        private static int getDefaultPic(int type) {
                switch (type) {
                case 0:// 电影
                        return R.drawable.img_default_movie;
                case 1:// 妹子
                        return R.drawable.img_default_meizi;
                case 2:// 书籍
                        return R.drawable.img_default_book;
                }
                return R.drawable.img_default_meizi;
        }

        /**
         * 书籍列表图片
         */
        @BindingAdapter("android:showAlbumImg")
        public static void showAlbumImg(ImageView imageView, String url) {
                Glide.with(imageView.getContext())
                        .load(url)
                        .crossFade(500)
                        .override((int) CommonUtils.getDimens(R.dimen.albums_width), (int) CommonUtils.getDimens(R.dimen.albums_height))
                        .placeholder(R.drawable.img_default_book)
                        .error(R.drawable.img_default_book)
                        .into(imageView);
        }

        /**
         * 书籍列表图片
         */
        @BindingAdapter("android:showBookImg")
        public static void showBookImg(ImageView imageView, String url) {
                Glide.with(imageView.getContext())
                        .load(PreferenceManager.uniqueInstance().getBookCoverUrl()+url)
                        .crossFade(500)
                        .override((int) CommonUtils.getDimens(R.dimen.book_detail_width), (int) CommonUtils.getDimens(R.dimen.book_detail_height))
                        .placeholder(R.drawable.img_default_book)
                        .error(R.drawable.img_default_book)
                        .into(imageView);
        }

        /**
         * 加载圆角图,暂时用到显示头像
         */
        @BindingAdapter("android:showAvatar")
        public static void displayCircle(ImageView imageView, String imageUrl) {
                Glide.with(imageView.getContext())
                        .load(imageUrl)
                        .crossFade(500)
                        .error(R.drawable.default_avata)
                        .transform(new GlideCircleTransform(imageView.getContext()))
                        .into(imageView);
        }

        /**
         * 书籍列表图片
         */
        @BindingAdapter("android:showMovieImg")
        public static void showMovieImg(ImageView imageView, String url) {
                Glide.with(imageView.getContext())
                        .load(Constants.MOVIE_COVER_PRE_URL+url)
                        .crossFade(500)
                        .override((int) CommonUtils.getDimens(R.dimen.movie_detail_width), (int) CommonUtils.getDimens(R.dimen.movie_detail_height))
                        .placeholder(R.drawable.default_movie)
                        .error(R.drawable.default_movie)
                        .into(imageView);
        }

        /**
         * 电影列表图片
         */
        @BindingAdapter("android:showRecImg")
        public static void showRecImg(ImageView imageView, String url) {
                Glide.with(imageView.getContext())
                        .load(url)
                        .crossFade(500)
                        .override((int) CommonUtils.getDimens(R.dimen.movie_detail_width), (int) CommonUtils.getDimens(R.dimen.movie_detail_height))
                        .placeholder(R.drawable.img_default_movie)
                        .error(R.drawable.img_default_movie)
                        .into(imageView);
        }


        /**
         * 详情页显示高斯背景图
         */
        @BindingAdapter("android:showImgBg")
        public static void showImgBg(ImageView imageView, String url) {
                displayGaussian(imageView.getContext(), PreferenceManager.uniqueInstance().getBookCoverUrl()+url, imageView);
        }

        /**
         * 显示高斯模糊效果（电影详情页）
         */
        private static void displayGaussian(Context context, String url, ImageView imageView) {
                // "23":模糊度；"4":图片缩放4倍后再进行模糊
                Glide.with(context)
                        .load(url)
                        .error(R.drawable.stackblur_default)
                        .placeholder(R.drawable.stackblur_default)
                        .crossFade(500)
                        .bitmapTransform(new BlurTransformation(context, 23, 4))
                        .into(imageView);
        }
}
