package com.example.sse_service.domain.controller;

import com.example.sse_service.domain.connection.SseConnectionPool;
import com.example.sse_service.domain.connection.model.UserSseConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import java.util.Map;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping()
public class SseApiController {

    private final SseConnectionPool sseConnectionPool;

    private final ObjectMapper objectMapper;

    @RequestMapping(path = {"", "/main"})
    public ModelAndView main(){
        return new ModelAndView("main");
    }

    @GetMapping(path = "/api/sse/connect/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseBodyEmitter connect(@PathVariable String id) {
        log.info("login user {}", id);

        var userSseConnection = UserSseConnection.connect(
                id,
                sseConnectionPool,
                objectMapper);

        sseConnectionPool.addSession(userSseConnection.getUniqueKey(), userSseConnection);

        return userSseConnection.getSseEmitter();
    }

    @GetMapping("/api/sse/push-event/{id}")
    public void pushEvent(@PathVariable String id) {
        var userSseConnection = sseConnectionPool.getSession(id);

        Optional.ofNullable(userSseConnection)
                .ifPresent(it -> {
                    it.sendMessage(Map.of("id", it.getUniqueKey(), "message", "helloworld"));
                });
    }

    // 전체 발송
    @GetMapping("/api/sse/broadCast")
    public void broadCast() {
        sseConnectionPool.broadCast();
    }
}