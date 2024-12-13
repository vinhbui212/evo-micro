package com.evo.common.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Response<T> implements Serializable {
    protected T data;
    private boolean success;
    private int code;
    private String message;
    private long timestamp;
    @JsonIgnore
    private RuntimeException exception;

    public static <T> Response<T> fail(RuntimeException exception) {
        Response<T> response = new Response<>();
        response.setSuccess(false);
        response.setException(exception);
        return response;
    }

    public static<T> Response<T> success(String message, T data){
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

}
