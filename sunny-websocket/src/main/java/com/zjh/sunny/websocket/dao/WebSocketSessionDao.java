package com.zjh.sunny.websocket.dao;

import com.zjh.sunny.websocket.session.WebSocketSession;

public interface WebSocketSessionDao {

    WebSocketSession getSessionByUserId(long userId);

    void saveSession(WebSocketSession webSocketSession);

    void deleteSessionByUserId(long userId);
}
