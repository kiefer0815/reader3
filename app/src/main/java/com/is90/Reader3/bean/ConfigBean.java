package com.is90.Reader3.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kiefer on 2017/6/20.
 */

public class ConfigBean implements Serializable {
        private int id;
        private int version_code;
        private String app_update;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getVersion_code() {
                return version_code;
        }

        public void setVersion_code(int version_code) {
                this.version_code = version_code;
        }

        public String getApp_update() {
                return app_update;
        }

        public void setApp_update(String app_update) {
                this.app_update = app_update;
        }

        private List<NovelsBean>  hotNovels;

        public List<NovelsBean> getHotNovels() {
                return hotNovels;
        }

        public void setHotNovels(List<NovelsBean> hotNovels) {
                this.hotNovels = hotNovels;
        }
}
