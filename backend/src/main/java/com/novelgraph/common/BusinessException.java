package com.novelgraph.common;

import lombok.Getter;

/**
 * 业务异常
 * 用于在 Service 层抛出带有业务语义的错误，由全局异常处理器统一处理。
 *
 * @author novelgraph
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
