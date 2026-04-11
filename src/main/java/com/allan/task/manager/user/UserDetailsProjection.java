package com.allan.task.manager.user;

import java.util.UUID;

public interface UserDetailsProjection {
    String getUsername();
    String getPassword();
    UUID getRoleId();
    String getAuthority();
}
