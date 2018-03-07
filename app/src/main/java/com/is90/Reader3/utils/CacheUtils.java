

package com.is90.Reader3.utils;

import com.alibaba.fastjson.JSON;
import com.is90.Reader3.AppContext;

import java.io.*;

/**
 * 简单对象缓存的工具类，默认使用对象的simple name作为文件名
 *
 * @author TangWei at: http://daveztong.github.io/
 */

public class CacheUtils extends LruObjectCache {

    private static CacheUtils cacheUtils;


    public CacheUtils() {
        super(1);
    }

    public static CacheUtils uniqueInstance(){
       CacheUtils result = cacheUtils;
        if (result==null){
            synchronized (CacheUtils.class){
                result=cacheUtils;
                if (result==null){
                    result=cacheUtils=new CacheUtils();
                }
            }
        }

        return result;
    }

    /**
     * get from memory cache first use the class canonical name,if not found then read from disk
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(Class<T> clazz) {
        return get(clazz, clazz.getCanonicalName());
    }

    /**
     * get from memory cache first,if not found then read from disk
     *
     * @param clazz
     * @param id
     * @param <T>
     * @param <ID>
     * @return
     */
    @Override
    public <T, ID> T get(Class<T> clazz, ID id) {
        T cachedObj = super.get(clazz, id);
        if (cachedObj == null) {
            cachedObj = read(clazz);
            if (cachedObj != null)
                put(clazz, id, cachedObj);
        }
        return cachedObj;
    }

    /**
     * save to memory cache and disk, use class canonical name as the key
     *
     * @param clazz
     * @param obj
     * @param <T>
     */
    public <T> void put(Class<T> clazz, T obj) {
        put(clazz, obj.getClass().getCanonicalName(), obj);
    }

    @Override
    public <T, ID> void put(Class<T> clazz, ID id, T data) {
        save(data);
        super.put(clazz, id, data);
    }

    /**
     * 将obj转换成json保存在/data/data/com.ruoshui.bethune/files/cache/{@link Class#getSimpleName()}.json 文件中
     *
     * @param obj
     */
    private static synchronized void save(Object obj) {
        if (obj == null) return;
        String filename = assembleFileName(obj.getClass().getSimpleName());
        FileWriter fileWriter = null;
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
            fileWriter.write(JSON.toJSONString(obj));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取 /data/data/com.ruoshui.bethune/files/cache/clazz.json 并转换成相应的对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T read(Class<T> clazz) {
        String filename = assembleFileName(clazz.getSimpleName());
        BufferedReader reader = null;
        FileReader fileReader;
        T retVal = null;

        try {
            File file = new File(filename);
            if (!file.exists())
                return retVal;

            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String str = stringBuilder.toString();
            if (StringUtils.isEmpty(str))
                return retVal;

            retVal = JSON.parseObject(stringBuilder.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return retVal;
    }

    public static void delete(Class<?> clazz) {
        String filename = assembleFileName(clazz.getSimpleName());
        File file = new File(filename);
        if (file.exists())
            file.delete();
    }

    /**
     * 删除/data/data/com.ruoshui.bethune/files/cache/下的所有文件
     */
    public static void deleteAll() {
        File dir = new File(AppContext.applicationContext.getFilesDir(), "/cache");
        if (!dir.exists())
            return;
        if(dir.listFiles() !=  null) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
    }

    /**
     * 组装文件名
     *
     * @param filename
     * @return
     */
    private static String assembleFileName(String filename) {
        String assembledFilename = AppContext.applicationContext.getFilesDir() + "/cache/" + filename + ".json";
        return assembledFilename;
    }

    /**
     * 创建缓存目录
     */
    public static void createCacheDir() {
        File file = new File(AppContext.applicationContext.getFilesDir() + "/cache");
        if (!file.exists())
            file.mkdirs();

        File file_download = new File(AppContext.applicationContext.getFilesDir() + "/download");
        if (!file_download.exists())
            file_download.mkdirs();
    }
}
