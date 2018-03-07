package com.is90.Reader3.utils;

/**
 * Created by kiefer on 2017/6/16.
 */

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LruObjectCache {

        private final int capacity;
        private final ConcurrentHashMap<Class<?>, Map<Object, Object>> classMaps =
                new ConcurrentHashMap<Class<?>, Map<Object, Object>>();

        public LruObjectCache(int capacity) {
                this.capacity = capacity;
        }

        public synchronized <T> void registerClass(Class<T> clazz) {
                Map<Object, Object> objectMap = classMaps.get(clazz);
                if (objectMap == null) {
                        objectMap = Collections.synchronizedMap(new LimitedLinkedHashMap<Object, Object>(capacity));
                        classMaps.put(clazz, objectMap);
                }
        }

        public <T, ID> T get(Class<T> clazz, ID id) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap == null) {
                        return null;
                }
                Object obj = objectMap.get(id);
                @SuppressWarnings("unchecked")
                T castObj = (T) obj;
                return castObj;
        }

        public <T, ID> void put(Class<T> clazz, ID id, T data) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap != null) {
                        objectMap.put(id, data);
                }
        }

        public <T> void clear(Class<T> clazz) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap != null) {
                        objectMap.clear();
                }
        }

        public void clearAll() {
                for (Map<Object, Object> objectMap : classMaps.values()) {
                        objectMap.clear();
                }
        }

        public <T, ID> void remove(Class<T> clazz, ID id) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap != null) {
                        objectMap.remove(id);
                }
        }

        public <T, ID> T updateId(Class<T> clazz, ID oldId, ID newId) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap == null) {
                        return null;
                }
                Object obj = objectMap.remove(oldId);
                if (obj == null) {
                        return null;
                }
                objectMap.put(newId, obj);
                @SuppressWarnings("unchecked")
                T castObj = (T) obj;
                return castObj;
        }

        public <T> int size(Class<T> clazz) {
                Map<Object, Object> objectMap = getMapForClass(clazz);
                if (objectMap == null) {
                        return 0;
                } else {
                        return objectMap.size();
                }
        }

        public int sizeAll() {
                int size = 0;
                for (Map<Object, Object> objectMap : classMaps.values()) {
                        size += objectMap.size();
                }
                return size;
        }

        private Map<Object, Object> getMapForClass(Class<?> clazz) {
                Map<Object, Object> objectMap = classMaps.get(clazz);
                if (objectMap == null) {
                        return null;
                } else {
                        return objectMap;
                }
        }

        /**
         * Little extension of the LimitedLinkedHashMap
         */
        private static class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

                private static final long serialVersionUID = -4566528080395573236L;
                private final int capacity;

                public LimitedLinkedHashMap(int capacity) {
                        super(capacity, 0.75F, true);
                        this.capacity = capacity;
                }

                @Override
                protected boolean removeEldestEntry(Entry<K, V> eldest) {
                        return size() > capacity;
                }
        }
}

