package com.takin.rpc.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

public class GuiceDI {

    private static Injector injector;

    public static void init() {
        injector = Guice.createInjector(new BindingModel());
    }
    
    public static <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

    public static void create(Module... modules) {
        injector = Guice.createInjector(modules);
    }

    public static void createInjector(Iterable<? extends Module> modules) {
        injector = Guice.createInjector(modules);
    }

    public <T> T injectMembers(T instance) {
        injector.injectMembers(instance);
        return instance;
    }

    public static <T> T getInstance(Key<T> key) {
        return injector.getInstance(key);
    }

    public static void createChildInjector(Module... modules) {
        injector.createChildInjector(modules);
    }

    public static void createChildInjector(Iterable<? extends Module> modules) {
        injector.createChildInjector(modules);
    }

}
