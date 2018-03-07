package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by kiefer on 2017/6/28.
 */

public class MoviesBean extends BaseObservable implements Serializable {
        private int id;
        private String movie_name;
        private String movie_price;
        private String movie_sha1_name;
        private String movie_file_size;

        @Bindable
        public String getMovie_file_id() {
                return movie_file_id;
        }

        public void setMovie_file_id(String movie_file_id) {
                this.movie_file_id = movie_file_id;
        }

        private String movie_file_id;

        @Bindable
        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        @Bindable
        public String getMovie_name() {
                return movie_name;
        }

        public void setMovie_name(String movie_name) {
                this.movie_name = movie_name;
        }

        @Bindable
        public String getMovie_price() {
                return movie_price;
        }

        public void setMovie_price(String movie_price) {
                this.movie_price = movie_price;
        }

        @Bindable
        public String getMovie_sha1_name() {
                return movie_sha1_name;
        }

        public void setMovie_sha1_name(String movie_sha1_name) {
                this.movie_sha1_name = movie_sha1_name;
        }

        @Bindable
        public String getMovie_file_size() {
                return movie_file_size;
        }

        public void setMovie_file_size(String movie_file_size) {
                this.movie_file_size = movie_file_size;
        }
}
