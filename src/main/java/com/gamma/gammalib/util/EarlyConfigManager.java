package com.gamma.gammalib.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.minecraft.launchwrapper.Launch;

import com.google.common.base.Throwables;

/**
 * Utility class for managing a mod's early configuration.
 * This isn't finished afaik, so don't use it. This was written when
 * I couldn't figure out a way to make GTNHLib's config system work
 * with certain Linux clients.
 */
public class EarlyConfigManager {

    public static final String configFileSuffix = "-early.properties";

    /**
     * Registers a config class.
     * 
     * @param configClass The config class to register.
     */
    public void registerConfig(Class<?> configClass) {
        EarlyConfig annotation = configClass.getAnnotation(EarlyConfig.class);
        if (annotation == null) {
            throw new EarlyConfigException(
                "Config class must be annotated with @EarlyConfig: " + configClass.getName());
        }

        ConfigFile file = new ConfigFile(annotation.value());

        for (Field field : configClass.getDeclaredFields()) {
            EarlyConfig.Name name = field.getAnnotation(EarlyConfig.Name.class);
            if (name == null) throw new EarlyConfigException(
                "Config fields must be annotated with @EarlyConfig.Name: " + field.getName());
            if (!Modifier.isStatic(field.getModifiers()))
                throw new EarlyConfigException("Config fields must be static: " + field.getName());
            if (Modifier.isFinal(field.getModifiers()))
                throw new EarlyConfigException("Config fields cannot be final: " + field.getName());
            if (!Modifier.isPublic(field.getModifiers()))
                throw new EarlyConfigException("Config fields must be public: " + field.getName());

            if (field.getType() == boolean.class) {
                EarlyConfig.DefaultBoolean defaultBoolean = field.getAnnotation(EarlyConfig.DefaultBoolean.class);
                file.entries.add(new BooleanConfigEntry(name.value(), field, defaultBoolean.value()));
            }
        }

        validateConfig(file);
        readConfig(file);
    }

    /**
     * Creates the game config folder if it doesn't exist and runs {@link #validateConfigFile(Path, ConfigFile)}.
     * 
     * @throws EarlyConfigException If the config folder cannot be created, the config folder is
     *                              not a directory, or if {@link #validateConfigFile(Path, ConfigFile)} fails.
     */
    private void validateConfig(ConfigFile config) {
        Path path = Launch.minecraftHome.toPath()
            .toAbsolutePath();
        Path configPath = path.resolve("config");
        File configDir = configPath.toFile();
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                throw new EarlyConfigException("Unable to create config folder.");
            }
        } else if (!configDir.isDirectory())
            throw new EarlyConfigException("Existing file present with name 'config', blocking directory usage.");

        validateConfigFile(configPath, config);
    }

    /**
     * Creates the mod's config file if it doesn't exist.
     * 
     * @throws EarlyConfigException If the config file cannot be created or is a directory.
     */
    private void validateConfigFile(Path configPath, ConfigFile config) {
        Path path = configPath.resolve(config + configFileSuffix);
        File configFile = path.toFile();
        config.file = configFile;
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    throw new EarlyConfigException("Unable to create config file.");
                }
            } catch (IOException e) {
                throw new EarlyConfigException("Failed to create config file", e);
            }
        } else if (configFile.isDirectory()) throw new EarlyConfigException(
            String.format("Existing directory present with name '%s', blocking file usage.", config.modID));
    }

    private void readConfig(ConfigFile file) {
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file.file)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new EarlyConfigException("Failed to read properties from config file" + e);
        }

        file.entries.forEach(entry -> entry.readFromProperties(properties));
    }

    private static class ConfigFile {

        private final String modID;
        private final List<ConfigEntry> entries = new ArrayList<>();
        private File file;

        private ConfigFile(String modID) {
            this.modID = modID;
        }
    }

    private static class BooleanConfigEntry extends ConfigEntry {

        final boolean defaultValue;

        private BooleanConfigEntry(String key, Field field, boolean defaultValue) {
            super(key, field);
            this.defaultValue = defaultValue;
        }

        @Override
        void readFromProperties(Properties properties) {
            String value = properties.getProperty(key, String.valueOf(defaultValue));
            try {
                field.set(null, Boolean.parseBoolean(value));
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private static abstract class ConfigEntry {

        final Field field;
        final String key;

        private ConfigEntry(String key, Field field) {
            this.key = key;
            this.field = field;
        }

        abstract void readFromProperties(Properties properties);
    }

    public static class EarlyConfigException extends Error {

        public EarlyConfigException(String message) {
            super(message);
        }

        public EarlyConfigException(String message, Throwable cause) {
            super(message, cause);
        }
    }

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
}
