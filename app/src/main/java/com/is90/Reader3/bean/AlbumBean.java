package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by kiefer on 2017/8/1.
 */

public class AlbumBean extends BaseObservable implements Serializable {
        private int id;
        private String name;
        private String image;
        private String filter;

        @Bindable
        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        @Bindable
        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        @Bindable
        public String getImage() {
                return image;
        }

        public void setImage(String image) {
                this.image = image;
        }

        @Bindable
        public String getFilter() {
                return filter;
        }

        public void setFilter(String filter) {
                this.filter = filter;
        }
}
