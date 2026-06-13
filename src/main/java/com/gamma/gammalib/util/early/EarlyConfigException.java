package com.gamma.gammalib.util.early;

public class EarlyConfigException extends Error {

    public EarlyConfigException(String message) {
        super(message);
    }

    public EarlyConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
