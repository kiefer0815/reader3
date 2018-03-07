package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by kiefer on 2017/9/19.
 */

public class RecommendBean extends BaseObservable implements Serializable {

        private int id;
        private String title;
        private String intro;
        private String download_url;
        private String pic;
        private int type;

        @Bindable
        public int getType() {
                return type;
        }

        public void setType(int type) {
                this.type = type;
        }

        @Bindable
        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        @Bindable
        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        @Bindable
        public String getIntro() {
                return intro;
        }

        public void setIntro(String intro) {
                this.intro = intro;
        }

        @Bindable
        public String getDownload_url() {
                return download_url;
        }

        public void setDownload_url(String download_url) {
                this.download_url = download_url;
        }

        @Bindable
        public String getPic() {
                return pic;
        }

        public void setPic(String pic) {
                this.pic = pic;
        }

}
