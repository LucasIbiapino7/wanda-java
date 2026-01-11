package com.cosmo.wanda_web.dto.bits;

public class ReturnsObjectsDTO {
    private String raw;
    private boolean invalidReturn;
    private String fallbackTo;

    public ReturnsObjectsDTO() {
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public boolean isInvalidReturn() {
        return invalidReturn;
    }

    public void setInvalidReturn(boolean invalidReturn) {
        this.invalidReturn = invalidReturn;
    }

    public String getFallbackTo() {
        return fallbackTo;
    }

    public void setFallbackTo(String fallbackTo) {
        this.fallbackTo = fallbackTo;
    }
}
