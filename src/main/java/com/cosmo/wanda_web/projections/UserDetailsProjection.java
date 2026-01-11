package com.cosmo.wanda_web.projections;

import com.cosmo.wanda_web.entities.ProfileType;

public interface UserDetailsProjection {
    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
    ProfileType getProfile();
}
