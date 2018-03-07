package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by kiefer on 2017/7/7.
 */

public class NovelsBean extends BaseObservable implements Serializable {
        private int id;
        private String book_sha1_file;
        private String book_sha1_image;
        private String book_name;
        private String author;
        private String state;
        private String intro;
        private String size;
        private String type;
        private String book_price;
        private String src;

        @Bindable
        public String getSrc() {
                return src;
        }

        public void setSrc(String src) {
                this.src = src;
        }

        @Bindable
        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        @Bindable
        public String getBook_sha1_file() {
                return book_sha1_file;
        }

        public void setBook_sha1_file(String book_sha1_file) {
                this.book_sha1_file = book_sha1_file;
        }

        @Bindable
        public String getBook_sha1_image() {
                return book_sha1_image;
        }

        public void setBook_sha1_image(String book_sha1_image) {
                this.book_sha1_image = book_sha1_image;
        }

        @Bindable
        public String getBook_name() {
                return book_name;
        }

        public void setBook_name(String book_name) {
                this.book_name = book_name;
        }

        @Bindable
        public String getAuthor() {
                return author;
        }

        public void setAuthor(String author) {
                this.author = author;
        }

        @Bindable
        public String getState() {
                return state;
        }

        public void setState(String state) {
                this.state = state;
        }

        @Bindable
        public String getIntro() {
                return intro;
        }

        public void setIntro(String intro) {
                this.intro = intro;
        }

        @Bindable
        public String getSize() {
                return size;
        }

        public void setSize(String size) {
                this.size = size;
        }

        @Bindable
        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        @Bindable
        public String getBook_price() {
                return book_price;
        }

        public void setBook_price(String book_price) {
                this.book_price = book_price;
        }
}
