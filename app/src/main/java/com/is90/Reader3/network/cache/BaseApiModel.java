package com.is90.Reader3.network.cache;


import java.util.ArrayList;

/**
 * Created by kiefer on 16/5/17.
 */
public class BaseApiModel {
        private ArrayList<BaseApiMasterModel> data;

        public ArrayList<BaseApiMasterModel> getData() {
                return data;
        }

        public void setData(ArrayList<BaseApiMasterModel> data) {
                this.data = data;
        }
        public static class BaseApiMasterModel{
                private String api;

                public String getApi() {
                        return api;
                }

                public void setApi(String api) {
                        this.api = api;
                }

                public int getTime() {
                        return time;
                }

                public void setTime(int time) {
                        this.time = time;
                }

                private int time;
        }
}
