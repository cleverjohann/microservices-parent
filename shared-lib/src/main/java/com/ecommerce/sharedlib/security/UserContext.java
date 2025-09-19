package com.ecommerce.sharedlib.security;

import lombok.Getter;

public class UserContext {

    private static final ThreadLocal<UserInfo> userInfoHolder = new ThreadLocal<>();

    public static void setCurrentUser(Long userId, String email) {
        userInfoHolder.set(new UserInfo(userId, email));
    }

    public static UserInfo getCurrentUser() {
        return userInfoHolder.get();
    }

    public static Long getCurrentUserId() {
        UserInfo user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUserEmail() {
        UserInfo user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static void clear() {
        userInfoHolder.remove();
    }

    @Getter
    public static class UserInfo {
        private final Long userId;
        private final String email;

        public UserInfo(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }

    }
}