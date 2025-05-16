package com.example.foodorderapp.websocket;

import android.util.Log;

import com.example.foodorderapp.ultis.ultis;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocketClient webSocketClient;

    private static final String WS_URL = ultis.WEBSOCKET_URL;

    public interface OnMessageListener {
        void onMessage(String message);
    }

    private OnMessageListener onMessageListener;

    private WebSocketManager() {
        webSocketClient = new WebSocketClient(WS_URL, new WebSocketClient.WebSocketCallback() {
            @Override
            public void onConnected() {
                Log.d("WebSocketManager", "WebSocket connected");
            }

            @Override
            public void onMessage(String message) {
                Log.d("WebSocketManager", "Received message: " + message);
                if (onMessageListener != null) {
                    onMessageListener.onMessage(message);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("WebSocketManager", "WebSocket error: " + error);
            }

            @Override
            public void onClosed(String reason) {
                Log.d("WebSocketManager", "WebSocket closed: " + reason);
            }
        });
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void sendCancelOrder(int storeId, String orderId, String reason) {
        if (webSocketClient != null) {
            webSocketClient.sendCancelOrderMessage(storeId, orderId, reason);
        }
    }

    public void close() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
        instance = null;
    }

    // Cho phép đăng ký listener bên ngoài
    public void setOnMessageListener(OnMessageListener listener) {
        this.onMessageListener = listener;
    }

    // Nếu cần xóa listener
    public void removeOnMessageListener() {
        this.onMessageListener = null;
    }
}
