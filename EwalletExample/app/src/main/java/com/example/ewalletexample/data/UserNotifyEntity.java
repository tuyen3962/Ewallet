package com.example.ewalletexample.data;

public class UserNotifyEntity {
    private long notifyId;
    private String userId;
    private int serviceType;
    private String title;
    private String content;
    private String time;
    private int status;
    private long timemilliseconds;

    public long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(long notifyId) {
        this.notifyId = notifyId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimemilliseconds() {
        return timemilliseconds;
    }

    public void setTimemilliseconds(long timemilliseconds) {
        this.timemilliseconds = timemilliseconds;
    }
}
