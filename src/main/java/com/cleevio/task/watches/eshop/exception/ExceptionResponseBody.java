/*
 * Copyright (c) 2021, Anna Drevikovska.
 */

package com.cleevio.task.watches.eshop.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public final class ExceptionResponseBody {

    private final Instant timestamp;
    private final HttpStatus httpStatus;
    private final String type;
    private final String message;
    private final String path;
    private final List<String> errors;

    private ExceptionResponseBody(Instant timestamp,
                                 HttpStatus httpStatus,
                                 String type,
                                 String message,
                                 String path,
                                 List<String> errors) {
        this.timestamp = timestamp;
        this.httpStatus = httpStatus;
        this.type = type;
        this.message = message;
        this.path = path;
        this.errors = List.copyOf(errors);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<String> getErrors() {
        return errors;
    }

    public static Builder builder(HttpStatus httpStatus, String type, String message) {
        return new Builder(httpStatus, type, message);
    }

    public static final class Builder {

        private final Instant timestamp;
        private final HttpStatus httpStatus;
        private final String type;
        private final String message;
        private String path;
        private List<String> errors;

        public Builder(HttpStatus httpStatus, String type, String message) {
            this.timestamp = Instant.now();
            this.httpStatus = httpStatus;
            this.type = type;
            this.message = message;
        }

        public Builder atPath(String path) {
            this.path = path;
            return this;
        }

        public Builder hasErrors(List<String> errors) {
            this.errors = List.copyOf(errors);
            return this;
        }

        public ExceptionResponseBody build() {
            return new ExceptionResponseBody(
                    timestamp,
                    httpStatus,
                    type,
                    message,
                    path,
                    (errors != null) ? List.copyOf(errors) : Collections.emptyList()
            );
        }

    }

}
