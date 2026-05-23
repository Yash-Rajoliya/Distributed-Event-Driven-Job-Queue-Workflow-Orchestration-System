package com.djqueue.common.multitenancy;

public class TenantContext {

    private static final ThreadLocal<String> tenant = new ThreadLocal<>();

    public static void set(String t) { tenant.set(t); }
    public static String get() { return tenant.get(); }
    public static void clear() { tenant.remove(); }
}