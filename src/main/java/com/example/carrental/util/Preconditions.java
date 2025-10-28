package com.example.carrental.util;

import com.example.carrental.exception.ValidationException;

public final class Preconditions {
    private Preconditions() {}
    public static void notNull(Object obj, String message) {
        if (obj == null) throw new ValidationException(message);
    }
}
