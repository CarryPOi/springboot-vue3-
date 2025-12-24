package com.example.fitness.controller;

import lombok.Data;

/**
 * 通用返回结果类（前后端交互统一格式）
 * @param <T> 数据泛型
 */
@Data
public class Result<T> {
    // 返回码：0成功，1失败
    private Integer code;
    // 返回消息
    private String msg;
    // 返回数据
    private T data;

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }


}