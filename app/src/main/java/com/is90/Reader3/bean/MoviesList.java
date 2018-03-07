package com.is90.Reader3.bean;

import java.util.List;

/**
 * Created by kiefer on 2017/6/28.
 */

public class MoviesList {

        public List<MoviesBean> getList() {
                return list;
        }

        public void setList(List<MoviesBean> list) {
                this.list = list;
        }

        private List<MoviesBean> list;
}
