package com.is90.Reader3.bean;

import java.util.List;

/**
 * Created by kiefer on 2017/6/17.
 */

public class BooksList {
        public List<BooksBean> getList() {
                return list;
        }

        public void setList(List<BooksBean> list) {
                this.list = list;
        }

        private List<BooksBean> list;
}
