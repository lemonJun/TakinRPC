package com.takin.rpc.server;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Launcher;

/**
 * A class for load jar
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
@SuppressWarnings("restriction")
public class GlobalClassLoader {

    private static Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);

    private static Method addURL;

    static {
        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
        } catch (Exception e) {
            e.printStackTrace();
        }
        addURL.setAccessible(true);
    }

    private static URLClassLoader system = (URLClassLoader) getSystemClassLoader();

    private static URLClassLoader ext = (URLClassLoader) getExtClassLoader();

    public static ClassLoader getSystemClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    public static ClassLoader getExtClassLoader() {
        return getSystemClassLoader().getParent();
    }

    public static void addURL2SystemClassLoader(URL url) throws Exception {
        try {
            logger.info("append jar to classpath:" + url.toString());
            addURL.invoke(system, new Object[] { url });
        } catch (Exception e) {
            throw e;
        }
    }

    public static void addURL2ExtClassLoader(URL url) throws Exception {
        try {
            logger.info("append jar to classpath:" + url.toString());
            addURL.invoke(ext, new Object[] { url });
        } catch (Exception e) {
            throw e;
        }
    }

    public static void addSystemClassPath(String path) throws Exception {
        try {
            URL url = new URL("file", "", path);
            addURL2SystemClassLoader(url);
        } catch (MalformedURLException e) {
            throw e;
        }
    }

    public static void addExtClassPath(String path) throws Exception {
        try {
            URL url = new URL("file", "", path);
            addURL2ExtClassLoader(url);
        } catch (MalformedURLException e) {
            throw e;
        }
    }

    public static void addSystemClassPathFolder(String... dirs) throws Exception {
        List<String> jarList = FileHelper.getUniqueLibPath(dirs);
        for (String jar : jarList) {
            addSystemClassPath(jar);
        }
    }

    public static void addURL2ExtClassLoaderFolder(String... dirs) throws Exception {
        List<String> jarList = FileHelper.getUniqueLibPath(dirs);
        for (String jar : jarList) {
            addExtClassPath(jar);
        }
    }

    public static URL[] getBootstrapURLs() {
        return Launcher.getBootstrapClassPath().getURLs();
    }

    public static URL[] getSystemURLs() {
        return system.getURLs();
    }

    public static URL[] getExtURLs() {
        return ext.getURLs();
    }
}