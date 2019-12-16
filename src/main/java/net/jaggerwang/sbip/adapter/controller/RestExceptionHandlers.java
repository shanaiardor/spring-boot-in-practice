package net.jaggerwang.sbip.adapter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import net.jaggerwang.sbip.adapter.controller.dto.JsonDto;
import net.jaggerwang.sbip.api.exception.UnauthenticatedException;
import net.jaggerwang.sbip.api.exception.UnauthorizedException;
import net.jaggerwang.sbip.usecase.exception.NotFoundException;
import net.jaggerwang.sbip.usecase.exception.UsecaseException;

@ControllerAdvice
public class RestExceptionHandlers {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonDto> handle(Exception exception) {
        exception.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JsonDto("fail", exception.toString()));
    }

    @ExceptionHandler(UsecaseException.class)
    public ResponseEntity<JsonDto> handle(UsecaseException exception) {
        return ResponseEntity.ok().body(new JsonDto("fail", exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<JsonDto> handle(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new JsonDto("not_found", exception.getMessage()));
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<JsonDto> handle(UnauthenticatedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new JsonDto("unauthenticated", exception.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<JsonDto> handle(UnauthorizedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JsonDto("unauthorized", exception.getMessage()));
    }
}