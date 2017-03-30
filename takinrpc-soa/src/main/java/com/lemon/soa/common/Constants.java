package com.lemon.soa.common;

import java.util.regex.Pattern;

/**
 * Constants
 * 
 * me
 */
public class Constants {

    /**
     * Default encoding
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String BACKUP_KEY = "backup";
    public static final String DEFAULT_KEY_PREFIX = "default.";
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final String ANY_VALUE = "*";
    public static final String ANYHOST_VALUE = "0.0.0.0";
    public static final String ANYHOST_KEY = "anyhost";
    public static final String LOCALHOST_KEY = "localhost";
    public static final String VERSION_KEY = "version";
    public static final String INTERFACE_KEY = "interface";
    public static final String GROUP_KEY = "group";
    public static final String DEFAULT_PROTOCOL_VALUE = "neural";
    public static final int DEFAULT_PORT_VALUE = 10913;

}
