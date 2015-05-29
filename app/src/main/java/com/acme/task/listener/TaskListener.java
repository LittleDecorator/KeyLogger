package com.acme.task.listener;

public interface TaskListener<T> {

    void taskComplete(T data);

}
