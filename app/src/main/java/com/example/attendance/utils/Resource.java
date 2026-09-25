package com.example.attendance.utils;

public class Resource<T> {

    public enum Status {
        SUCCESS,
        ERROR,
        LOADING,
        OFFLINE_SAVED
    }

    public final Status status;
    public final T data;
    public final String message;

    private Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> offlineSaved(String message) {
        return new Resource<>(Status.OFFLINE_SAVED, null, message);
    }
}
