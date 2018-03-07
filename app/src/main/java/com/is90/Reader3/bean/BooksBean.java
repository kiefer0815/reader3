package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;

import java.io.Serializable;

/**
 * Created by jingbin on 2016/12/15.
 */

public class BooksBean extends BaseObservable implements Serializable {
        private int id;
        private String book_name;
        private String book_price;
        private String book_image;
        private String book_md5_name;

        @Bindable
        public String getBook_md5_name() {
                return book_md5_name;
        }

        public void setBook_md5_name(String book_md5_name) {
                this.book_md5_name = book_md5_name;
        }

        @Bindable
        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        @Bindable
        public String getBook_name() {
                return book_name;
        }

        public void setBook_name(String book_name) {
                this.book_name = book_name;
        }

        @Bindable
        public String getBook_price() {
                return book_price;
        }

        public void setBook_price(String book_price) {
                this.book_price = book_price;
        }

        @Bindable
        public String getBook_image() {
                return book_image;
        }

        public void setBook_image(String book_image) {
                this.book_image = book_image;
        }
}
