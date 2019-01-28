package com.lchli.lottery.util;

import java.util.Set;

public final class RoleUtils {

    public static boolean hasRole(String role, Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.contains(role);
    }
}
