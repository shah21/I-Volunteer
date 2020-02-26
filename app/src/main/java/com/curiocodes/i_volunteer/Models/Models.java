package com.curiocodes.i_volunteer.Models;

import android.net.Uri;

import java.util.Date;

public class Models {
    public static class Posts{
        private String name;
        private String uri;
        private String title;
        private String content;
        private Date date;
        private String place;
        private String uid;
        private String contact;
        private String key;
        private boolean isTrue;


        public Posts(String key,String name, String uri, String title, String content, Date date, String place, String uid,String contact, boolean isTrue) {
            this.name = name;
            this.key = key;
            this.uri = uri;
            this.title = title;
            this.contact = contact;
            this.content = content;
            this.date = date;
            this.place = place;
            this.uid = uid;
            this.isTrue = isTrue;
        }

        public String getKey() {
            return key;
        }

        public String getUid() {
            return uid;
        }

        public String getContact() {
            return contact;
        }

        public Posts() { }


        public String getName() {
            return name;
        }

        public String getUri() {
            return uri;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public Date getDate() {
            return date;
        }

        public boolean isTrue() {
            return isTrue;
        }

        public String getPlace() {
            return place;
        }
    }

    public static class Request{
        private String requestId;
        private Date requested;
        private boolean accept;

        public Request(){}

        public Request(String requestId, Date requested, boolean accept) {
            this.requestId = requestId;
            this.requested = requested;
            this.accept = accept;
        }

        public String getRequestId() {
            return requestId;
        }

        public Date getRequested() {
            return requested;
        }

        public boolean isAccept() {
            return accept;
        }
    }


}
