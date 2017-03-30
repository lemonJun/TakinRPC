package com.lemon.soa.common.utils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isRealNumber(String number) {
        return number.matches("-?[0-9]+.*[0-9]*");
    }

    public static String convertToReflectGetMethod(String propertyName) {
        return "get" + toFirstUpChar(propertyName);
    }

    public static String convertToReflectSetMethod(String propertyName) {
        return "set" + toFirstUpChar(propertyName);
    }

    public static String toFirstUpChar(String target) {
        StringBuilder sb = new StringBuilder(target);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String getFileSuffixName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static boolean isNotEmpty(String stringValue) {
        if (null == stringValue || "".equals(stringValue.trim())) {
            return false;
        }
        return true;
    }

    public static String getPackageByPath(File classFile, String exclude) {
        if (classFile == null || classFile.isDirectory()) {
            return null;
        }
        String path = classFile.getAbsolutePath().replace('\\', '/');
        path = path.substring(path.indexOf(exclude) + exclude.length()).replace('/', '.');
        if (path.startsWith(".")) {
            path = path.substring(1);
        }
        if (path.endsWith(".")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.substring(0, path.lastIndexOf('.'));
    }

    public static String get(String regex, String content, int groupIndex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    public static String get(Pattern pattern, String content, int groupIndex) {
        if (null == content) {
            return null;
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }

    public static <T extends Collection<String>> T findAll(Pattern pattern, String content, int group, T collection) {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            collection.add(matcher.group(group));
        }
        return collection;
    }

    public static <T extends Collection<String>> T findAll(String regex, String content, int group, T collection) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return findAll(pattern, content, group, collection);
    }

    public static boolean isEmpty(Object obj) {
        return isEmptyOrBlank(obj, false);
    }

    @SuppressWarnings("rawtypes")
    private static boolean isEmptyOrBlank(Object obj, boolean trim) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            String ss = (String) obj;
            return (trim ? ss.trim() : ss).length() == 0;
        }
        if (obj instanceof Object[]) {
            Object[] oo = (Object[]) obj;
            for (int i = 0; i < oo.length; i++) {
                if (!isEmptyOrBlank(oo[i], trim)) {
                    return false;
                }
            }
            return true;
        }
        if (obj instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> oo = (Collection<Object>) obj;
            for (Iterator<Object> i = oo.iterator(); i.hasNext();) {
                if (!isEmptyOrBlank(i.next(), trim)) {
                    return false;
                }
            }
            return true;
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        return false;
    }

    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);
        return mer.find();
    }

}