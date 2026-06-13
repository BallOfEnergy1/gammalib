package com.gamma.gammalib.util.early;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EarlyConfig {

    /**
     * Mod ID for the mod registering this config.
     *
     * @return The mod ID for the mod.
     */
    String value();

    /**
     * Name of the config entry.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @interface Name {

        String value();
    }

    /**
     * Default boolean value for the config entry.
     */
    // Only toggles for now. TODO: Add more.
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    @interface DefaultBoolean {

        boolean value();
    }
}
