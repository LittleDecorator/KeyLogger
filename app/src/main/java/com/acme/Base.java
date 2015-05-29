package com.acme;

import android.app.Application;
import android.content.Context;

public class Base extends Application{

    private static Base instance;
    private static String id;

    public Base() {
        instance = this;
        id = "";
    }

    public static Context getContext() {
        return instance;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Base.id = id;
    }
}
