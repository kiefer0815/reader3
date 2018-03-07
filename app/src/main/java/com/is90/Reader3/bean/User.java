package com.is90.Reader3.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kiefer on 2017/6/16.
 */

public class User extends BaseObservable implements Serializable {

        private Long id;

        private String name;

        private String avatar;

        private Date birth;
        // 0 - male , 1 - female
        private short sex;


        @Bindable
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
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
        public String getAvatar() {
                return avatar;
        }

        public void setAvatar(String avatar) {
                this.avatar = avatar;
        }
        @Bindable
        public Date getBirth() {
                return birth;
        }

        public void setBirth(Date birth) {
                this.birth = birth;
        }

        @Bindable
        public short getSex() {
                return sex;
        }

        public void setSex(short sex) {
                this.sex = sex;
        }

        private String token;

        @Bindable
        public String getToken() {
                return token;
        }

        public void setToken(String token) {
                this.token = token;
        }

        @Override
        public String toString() {
                return "User{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", avatar='" + avatar + '\'' +
                        ", birth=" + birth +
                        ", sex=" + sex +
                        '}';
        }
}
