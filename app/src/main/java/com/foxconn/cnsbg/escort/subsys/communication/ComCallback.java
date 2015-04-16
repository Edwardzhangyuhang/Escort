package com.foxconn.cnsbg.escort.subsys.communication;

public interface ComCallback<T> {
    public void onSuccess(T value);
    public void onFailure(Throwable value);
}