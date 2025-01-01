package com.FindersKeepers.backend.config.security.user;

public class LimitValidatorThreadLocalStorage {
    private static final ThreadLocal<Boolean> threadLocalId = ThreadLocal.withInitial(() -> null);

    public static Boolean getStatus() {
        return threadLocalId.get();
    }

    public static void setStatus(Boolean status) {
        threadLocalId.set(status);
    }

    public static void clearStatus() {
        threadLocalId.remove();
    }
}
