package io.github.pureza.warbots.resources;

import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Utility class to read .properties files.
 */
public class PropertiesReader {

    /** The properties */
    private Properties properties;


    /**
     * Creates a new reader for the given properties
     */
    public PropertiesReader(Properties properties) {
        this.properties = properties;
    }


    /**
     * Reads a boolean value from the properties
     *
     * @throws IllegalArgumentException if the value is not 'true' or 'false'
     * @throws NoSuchElementException if the property does not exist
     */
    public boolean getBoolean(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value != null) {
            if (value.equalsIgnoreCase("true")) {
                return true;
            } else if (value.equalsIgnoreCase("false")) {
                return false;
            } else {
                throw new IllegalArgumentException("Invalid boolean value for property: " + propertyName);
            }
        } else {
            throw new NoSuchElementException(propertyName);
        }
    }


    /**
     * Reads an integer value from the properties
     *
     * @throws IllegalArgumentException if the value is not a valid integer
     * @throws NoSuchElementException if the property does not exist
     */
    public int getInt(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            throw new NoSuchElementException(propertyName);
        }
    }


    /**
     * Reads a double value from the properties
     *
     * @throws IllegalArgumentException if the value is not a valid double
     * @throws NoSuchElementException if the property does not exist
     */
    public double getDouble(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value != null) {
            return Double.parseDouble(value);
        } else {
            throw new NoSuchElementException(propertyName);
        }
    }


    /**
     * Reads a long value from the properties
     *
     * @throws IllegalArgumentException if the value is not a valid long
     * @throws NoSuchElementException if the property does not exist
     */
    public long getLong(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value != null) {
            return Long.parseLong(value);
        } else {
            throw new NoSuchElementException(propertyName);
        }
    }


    /**
     * Reads a string value from the properties
     *
     * @throws NoSuchElementException if the property does not exist
     */
    public String getString(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value != null) {
            return value;
        } else {
            throw new NoSuchElementException(propertyName);
        }
    }
}

