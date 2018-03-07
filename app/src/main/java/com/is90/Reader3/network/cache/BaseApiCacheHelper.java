package com.is90.Reader3.network.cache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiefer on 16/5/17.
 */
public class BaseApiCacheHelper {
        protected static HashMap<String, Integer> cacheTimeMap = new HashMap<>();

        public static void initCacheTimeFromApi(ArrayList<BaseApiModel.BaseApiMasterModel> list) {
                cacheTimeMap.clear();
                if(list!=null && list.size()>0){
                        for(BaseApiModel.BaseApiMasterModel masterBean :list ){
                                cacheTimeMap.put(masterBean.getApi(),masterBean.getTime());
                        }
                }
        }

        // 获取对应接口缓存
        public static Integer getCacheTimeFromApi(String key) {
                int time =0;
                try {
                        time = cacheTimeMap.get(key);
                }catch (Exception e){

                }
                return time;
        }

        public static void put(String key ,int value){
                cacheTimeMap.put(key,value);
        }
}
