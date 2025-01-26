package com.cosmo.wanda_web.dto.function;

public class FunctionRequestDTO {
    private String code;

    public FunctionRequestDTO() {
    }

    public FunctionRequestDTO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
