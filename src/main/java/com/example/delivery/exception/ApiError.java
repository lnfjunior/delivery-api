package com.example.delivery.exception;

import java.time.OffsetDateTime;

public class ApiError {
    public String path;
    public int status;
    public String error;
    public String message;
    public OffsetDateTime timestamp = OffsetDateTime.now();
}
