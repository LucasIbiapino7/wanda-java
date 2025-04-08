package com.cosmo.wanda_web.dto.function;

import com.cosmo.wanda_web.services.utils.AssistantStyle;

public class FunctionRequestDTO {
    private String code;
    private String assistantStyle;

    public FunctionRequestDTO() {
    }

    public FunctionRequestDTO(String code) {
        this.code = code;
        this.assistantStyle = null;
    }

    public FunctionRequestDTO(String code, String assistantStyle) {
        this.code = code;
        this.assistantStyle = assistantStyle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAssistantStyle() {
        return assistantStyle;
    }

    public void setAssistantStyle(String assistantStyle) {
        this.assistantStyle = assistantStyle;
    }
}
