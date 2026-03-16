package com.cosmo.wanda_web.infra.dtos;

import java.util.ArrayList;
import java.util.List;

public class FunctionInfo {
    private String name;
    private String description;
    private List<String> signature = new ArrayList<>();

    public FunctionInfo() {
    }

    public FunctionInfo(String name, String description, List<String> signature) {
        this.name = name;
        this.description = description;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSignature() {
        return signature;
    }
}
