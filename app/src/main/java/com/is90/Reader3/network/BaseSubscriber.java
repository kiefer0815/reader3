

package com.is90.Reader3.network;

import com.is90.Reader3.bean.DataModel;
import com.is90.Reader3.exception.*;
import rx.Subscriber;

/**
 * @author TangWei at: http://daveztong.github.io/
 */
public abstract class BaseSubscriber<T> extends Subscriber<DataModel<T>> {

    private boolean pageable = false;

    public BaseSubscriber() {
    }

    public BaseSubscriber(boolean pageable) {
        this.pageable = pageable;
    }


    @Override
    public void onCompleted() {
        onFinally(null);
    }

    @Override
    public void onError(Throwable e) {
        onFinally(e);
    }

    @Override
    public void onNext(DataModel<T> dataModel) {
        try {
            filter(dataModel);
             onSuccess(dataModel.getData());
        } catch (Exception e) {
            onError(e);
        }
    }

    public void onFinally(Throwable e) {

    }

    public void onSuccess(T data) {

    }



    protected void filter(DataModel<T> dataModel) throws Exception {
        if (dataModel == null) {
            throw new ServerException(520, "服务器错误，没有返回数据");
        }

        int status = dataModel.getStatus();
        HttpStatus httpStatus = HttpStatus.valueOf(dataModel.getStatus());
        if (httpStatus == null) {
            throw new RuntimeException(dataModel.getErrorMsg());
        }
        String errMsg = dataModel.getErrorMsg();
        switch (httpStatus) {
            case NONE:
                break;
            case OK:
                break;
            case NOT_LOGIN:
                throw new NotLoginException(errMsg);
            case USERNAME_PWD_WRONG:
                throw new LoginErrorException(errMsg);
            case FORBIDDEN:
                throw new NoPermissionException(errMsg);
            case NO_PERMISSION:
                throw new NoPermissionException(errMsg);
            case TOKEN_EXPIRED:
                 throw new TokenExpiredException(errMsg);
            case FIRST_LOGIN:
                break;
            case BAD_REQUEST:
                throw new RequestDataException(errMsg);
            case SERVER_ERROR:
                throw new ServerException(status, errMsg);
            case INTERNAL_SERVER_ERROR:
                throw new ServerException(status, errMsg);
            default:
                if (status < 200 || (status > 500 && status < 600)) {
                    throw new RuntimeException(errMsg);
                } else {
                    throw new RequestDataException(errMsg);
                }
        }
    }
}
