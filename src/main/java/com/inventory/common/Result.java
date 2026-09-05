package com.inventory.common;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private String timestamp;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now().toString();
    }

    public static <T> Result<T> success(T data) { return new Result<>(200, "success", data); }
    public static <T> Result<T> error(Integer code, String message) { return new Result<>(code, message, null); }
}