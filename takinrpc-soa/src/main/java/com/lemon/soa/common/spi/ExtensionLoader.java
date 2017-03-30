package com.lemon.soa.common.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceConfigurationError;

import com.lemon.soa.chain.INeuralChain;

/**
 * Transformation of ServiceLoader based on JDK
 * 
 * me
 */
public final class ExtensionLoader<S> {

    public static final String PREFIX = "META-INF/services/neural/";
    public static final String DEFAULT_KEY = "default";

    // The class or interface representing the service being loaded
    private final Class<S> service;

    // The class loader used to locate, load, and instantiate providers
    private final ClassLoader loader;

    // The access control context taken when the ServiceLoader is created
    private final AccessControlContext acc;

    // Cached providers, in instantiation order
    private LinkedHashMap<String, Provider> providers = new LinkedHashMap<>();

    // The current lazy-lookup iterator
    private LazyIterator lookupIterator;

    private List<String> providerNames = new ArrayList<String>();

    public void reload() {
        providers.clear();
        lookupIterator = new LazyIterator(service, loader);
    }

    private ExtensionLoader(Class<S> svc, ClassLoader cl) {
        service = Objects.requireNonNull(svc, "Service interface cannot be null");
        loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
        acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
        reload();
    }

    private static void fail(Class<?> service, String msg, Throwable cause) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg, cause);
    }

    private static void fail(Class<?> service, String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg) throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }

    // Parse a single line from the given configuration file, adding the name on the line to the names list.
    private int parseLine(Class<?> service, URL u, BufferedReader r, int lc, List<String> names) throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                fail(service, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.') && (cp != '=')) {
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            if (!providers.containsKey(ln) && !names.contains(ln)) {
                names.add(ln);
            }
        }
        return lc + 1;
    }

    private Iterator<String> parse(Class<?> service, URL u) throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0)
                ;
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return names.iterator();
    }

    private final class Provider {
        private final String name;
        private final S service;

        public Provider(String name, S service) {
            this.name = name;
            this.service = service;
        }
    }

    // Private inner class implementing fully-lazy provider lookup
    private class LazyIterator implements Iterator<Provider> {

        Class<S> service;
        ClassLoader loader;
        Enumeration<URL> configs = null;
        Iterator<String> pending = null;
        String nextName = null;

        private LazyIterator(Class<S> service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        private boolean hasNextService() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) {
                try {
                    String fullName = PREFIX + service.getName();
                    if (loader == null) {
                        configs = ClassLoader.getSystemResources(fullName);
                    } else {
                        configs = loader.getResources(fullName);
                    }
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(service, configs.nextElement());
            }
            nextName = pending.next();
            return true;
        }

        private Provider nextService() {
            if (!hasNextService()) {
                throw new NoSuchElementException();
            }
            String cn = nextName;
            nextName = null;
            Class<?> c = null;
            String name = null;
            try {
                int i = cn.indexOf('=');
                if (i > 0) {
                    name = cn.substring(0, i).trim();
                    cn = cn.substring(i + 1).trim();
                } else {
                    name = DEFAULT_KEY;
                }
                c = Class.forName(cn, false, loader);
            } catch (ClassNotFoundException x) {
                fail(service, "Provider " + cn + " not found");
            }
            if (!service.isAssignableFrom(c)) {
                fail(service, "Provider " + cn + " not a subtype");
            }
            try {
                S p = service.cast(c.newInstance());
                if (name != null) {
                    cn = name + "=" + cn;
                }
                Provider provider = new Provider(name == null ? cn : name, p);
                providers.put(cn, provider);
                return provider;
            } catch (Throwable x) {
                fail(service, "Provider " + cn + " could not be instantiated", x);
            }
            throw new Error(); // This cannot happen
        }

        public boolean hasNext() {
            if (acc == null) {
                return hasNextService();
            } else {
                PrivilegedAction<Boolean> action = new PrivilegedAction<Boolean>() {
                    public Boolean run() {
                        return hasNextService();
                    }
                };
                return AccessController.doPrivileged(action, acc);
            }
        }

        public Provider next() {
            if (acc == null) {
                return nextService();
            } else {
                PrivilegedAction<Provider> action = new PrivilegedAction<Provider>() {
                    public Provider run() {
                        return nextService();
                    }
                };
                return AccessController.doPrivileged(action, acc);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Iterator<Provider> iterator() {
        return new Iterator<Provider>() {
            Iterator<Map.Entry<String, Provider>> knownProviders = providers.entrySet().iterator();

            public boolean hasNext() {
                if (knownProviders.hasNext()) {
                    return true;
                }
                return lookupIterator.hasNext();
            }

            public Provider next() {
                if (knownProviders.hasNext()) {
                    return knownProviders.next().getValue();
                }
                return lookupIterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    private S load(String name) {
        Iterator<Provider> iterator = iterator();
        while (iterator.hasNext()) {
            Provider p = iterator.next();
            if (name.equals(p.name)) {
                return p.service;
            }
        }
        return null;
    }

    public static <S> ExtensionLoader<S> load(Class<S> service, ClassLoader loader) {
        return new ExtensionLoader<>(service, loader);
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ExtensionLoader.load(service, cl);
    }

    public static <S> ExtensionLoader<S> loadInstalled(Class<S> service) {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        ClassLoader prev = null;
        while (cl != null) {
            prev = cl;
            cl = cl.getParent();
        }
        return ExtensionLoader.load(service, prev);
    }

    public static <S> S loadSPI(Class<S> service) {
        if (!service.isAnnotationPresent(SPI.class)) {
            fail(service, "Provider " + service + " is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
        }

        SPI spi = service.getAnnotation(SPI.class);
        String name = spi.value();

        return getExtensionLoader(service, name);
    }

    public static <S> S getExtensionLoader(Class<S> service, String name) {
        ExtensionLoader<S> loader = getExtensionLoader(service);
        return loader.load(name);
    }

    ////$NON-NLS-Support$
    public List<String> getSupportedExtensions() {
        if (providers == null) {
            return null;
        }
        if (providerNames.size() < 1) {
            for (String key : providers.keySet()) {
                providerNames.add(key);
            }
        }
        return providerNames;
    }

    //$NON-NLS-简单$
    @SuppressWarnings("unchecked")
    public <K> K getAdaptiveExtension() {
        return (K) load(DEFAULT_KEY);
    }

    @SuppressWarnings("unchecked")
    public <K> K getAdaptiveExtension(String name) {
        return (K) load(name);
    }

    //$NON-NLS-复杂$
    @SuppressWarnings("unchecked")
    public <K> List<K> getAdaptiveExtensions() {
        List<K> list = new ArrayList<K>();
        Iterator<Provider> iterator = iterator();
        while (iterator.hasNext()) {
            Provider p = iterator.next();
            if (p.name == null || p.name.length() < 1) {
                continue;
            }

            S s = load(p.name);
            SPI spi = s.getClass().getAnnotation(SPI.class);
            if (spi != null) {
                list.add((K) s);
            } else {
                fail(service, "Provider " + service + " is not extension, because WITHOUT @" + SPI.class.getSimpleName() + " Annotation!");
            }
        }

        Collections.sort(list, new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                return o1.getClass().getAnnotation(SPI.class).order() - o2.getClass().getAnnotation(SPI.class).order();
            }
        });

        return list;
    }

    public String toString() {
        return ExtensionLoader.class.getName() + "[" + service.getName() + "]";
    }

    public static void main(String[] args) {
        List<INeuralChain<String, String>> neuralChains = ExtensionLoader.getExtensionLoader(INeuralChain.class).getAdaptiveExtensions();
        for (INeuralChain<String, String> neuralChain : neuralChains) {
            System.out.println(neuralChain);
        }
    }

}
