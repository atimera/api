package com.atimera.api.constant;

import static com.atimera.api.constant.Permission.*;

public class Authority {
    public static final String[] USER_AUTHORITIES = {USER_READ};
    public static final String[] MANAGER_AUTHORITIES = {USER_READ, USER_UPDATE};
    public static final String[] ADMIN_AUTHORITIES = {USER_CREATE, USER_READ, USER_UPDATE};
    public static final String[] SUPER_ADMIN_AUTHORITIES = {USER_CREATE, USER_READ, USER_UPDATE, USER_DELETE};
}
