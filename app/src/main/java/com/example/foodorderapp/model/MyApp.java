package com.example.foodorderapp.model;

import android.app.Application;

import com.example.foodorderapp.websocket.WebSocketManager;
import com.example.foodorderapp.service.UserService;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WebSocketManager.init(this);
    }
}
