package com.FindersKeepers.backend.config.security.user;

public class RegisteredClientIdThreadLocalStorage {
    private static final ThreadLocal<String> threadLocalId = ThreadLocal.withInitial(() -> null);
    public static void setId(String id) {
        threadLocalId.set(id);
    }
    public static String getId() {
        return threadLocalId.get();
    }
    public static void clearId() {
        threadLocalId.remove();
    }
}