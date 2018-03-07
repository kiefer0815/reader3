package com.is90.Reader3.bean;

/**
 * Created by admin on 15/2/6.
 * <p>
 * 返回数据统一格式
 */
public class DataModel<T> {
    private int status;
    private String errorMsg;
    private T data;

    /**
     * see com.ruoshui.bethune.data.model.RpcResponse
     */
    private String headers;
    private String path;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
