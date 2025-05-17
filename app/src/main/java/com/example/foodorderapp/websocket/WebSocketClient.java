package com.example.foodorderapp.websocket;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {

    private static final String TAG = "MyWebSocketClient";
    private WebSocket webSocket;

    public interface WebSocketCallback {
        void onConnected();
        void onMessage(String message);
        void onError(String error);
        void onClosed(String reason);
    }

    private WebSocketCallback callback;

    public WebSocketClient(String url, WebSocketCallback callback) {
        this.callback = callback;
        initWebSocket(url);
    }

    private void initWebSocket(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, Response response) {
                Log.d(TAG, "Connected to WebSocket server");
                if (callback != null) callback.onConnected();
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                Log.d(TAG, "Received message: " + text);
                if (callback != null) callback.onMessage(text);
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage());
                if (callback != null) callback.onError(t.getMessage());
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                if (callback != null) callback.onClosed(reason);
            }
        });
    }

    public void registerStore(int storeId) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "register");
            json.put("storeId", storeId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (webSocket != null) {
            webSocket.send(json.toString());
            Log.d(TAG, "Sent register message: " + json.toString());
        } else {
            Log.e(TAG, "WebSocket not connected");
        }
    }
    public void registerUser(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "register_user");
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        if (webSocket != null) {
            webSocket.send(json.toString());
            Log.d(TAG, "Sent register user message: " + json.toString());
        } else {
            Log.e(TAG, "WebSocket not connected");
        }
    }
    public void sendCancelOrderMessage(int storeId, String orderId, String reason) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", "cancel_request");
            json.put("storeId", storeId);
            json.put("orderId", orderId);
            json.put("reason", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (webSocket != null) {
            webSocket.send(json.toString());
            Log.d(TAG, "Sent cancel message: " + json.toString());
        }
    }
    public void sendAccept(String type, String userId, String orderId, String reason) {
        try {
            JSONObject json = new JSONObject();
            json.put("type", type);
            json.put("userId", userId);
            json.put("orderId", orderId);
            if (reason != null) {
                json.put("reason", reason);
            }
            if (webSocket != null && webSocket.send(json.toString())) {
                Log.d("WebSocket", "Sent to user: " + json.toString());
            } else {
                Log.e("WebSocket", "WebSocket not connected or failed to send");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message, int storeId) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", message);
            json.put("storeId", storeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (webSocket != null) {
            webSocket.send(json.toString());
            Log.d(TAG, "Sent cancel message: " + json.toString());
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Client closed");
            webSocket = null;
        }
    }
}
