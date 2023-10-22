package com.example.sse_service.domain.connection.ifs;

import com.example.sse_service.domain.connection.model.UserSseConnection;

public interface ConnectionPoolIfs<T, R> {

    void addSession(T uniqueKey, R session);

    UserSseConnection getSession(T uniqueKey);

    void onCompletionCallback(R session);

    void broadCast();
}
