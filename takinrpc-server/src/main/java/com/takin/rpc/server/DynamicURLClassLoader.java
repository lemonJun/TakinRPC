package com.takin.rpc.server;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * A URLClassLoader for dynamic load class from jar
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 * <a href="http://blog.58.com/spat/">blog</a>
 * <a href="http://www.58.com">website</a>
 * 
 */
public class DynamicURLClassLoader {

    private static Method addURL;

    static {
        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
        } catch (Exception e) {
            e.printStackTrace();
        }
        addURL.setAccessible(true);
    }

    private URLClassLoader classLoader;

    public DynamicURLClassLoader() throws MalformedURLException {
        classLoader = new URLClassLoader(new URL[] { new URL("file", "", "") });
    }

    public void addURL(URL url) throws Exception {
        addURL.invoke(classLoader, new Object[] { url });
    }

    public void addURL(String path) throws Exception {
        URL url = new URL("file", "", path);
        addURL(url);
    }

    /**
     * add folder jars
     * @param path
     * @throws Exception 
     */
    public void addFolder(String... dirs) throws Exception {
        List<String> jarList = FileHelper.getUniqueLibPath(dirs);
        for (String jar : jarList) {
            addURL(jar);
        }
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }
}