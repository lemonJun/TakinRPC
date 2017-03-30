package com.takin.rpc.server;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class BindingModel extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.inSubpackage("com.takin.rpc.server"), Matchers.any(), new StatMethod());
        bindInterceptor(Matchers.inSubpackage("com.takin.emmet.reflect"), Matchers.any(), new StatMethod());
    }
}
