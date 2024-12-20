package com.imooc.bilibili.assistant.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConditionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        this.code = "500";
    }

}
