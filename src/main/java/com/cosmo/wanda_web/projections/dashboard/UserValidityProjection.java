package com.cosmo.wanda_web.projections.dashboard;

public interface UserValidityProjection {
    Long getUserId();
    Boolean getValid();
    Long getTotal();
}