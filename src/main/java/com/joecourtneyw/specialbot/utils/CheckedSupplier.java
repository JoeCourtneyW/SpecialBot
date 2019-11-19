package com.joecourtneyw.specialbot.utils;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
