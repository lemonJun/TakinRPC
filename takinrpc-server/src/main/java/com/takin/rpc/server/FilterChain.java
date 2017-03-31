package com.takin.rpc.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Singleton;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.takin.rpc.remoting.netty5.RemotingContext;

@Singleton
public class FilterChain {

    private final List<IFilter> filterList = Lists.newArrayList();
    private final AtomicBoolean once = new AtomicBoolean(false);

    @Inject
    private FilterChain() {
        
    }

    public void init() {
        if (once.compareAndSet(false, true)) {
            List<Class<?>> filterclass = GuiceDI.getInstance(Scaner.class).getFilterList();
            for (Class<?> cls : filterclass) {
                filterList.add((IFilter) GuiceDI.getInstance(cls));
            }
            Collections.sort(filterList, new FilterComparator());
        }
    }

    public Object dofilter(RemotingContext context) {
        for (IFilter filter : filterList) {
            try {
                Object ret = filter.filter(context);
                if (ret != null) {
                    return ret;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class FilterComparator implements Comparator<IFilter> {
        @Override
        public int compare(IFilter o1, IFilter o2) {
            if (o1 == null || o2 == null) {
                return 1;
            }
            int v1 = o1.getPriority();
            int v2 = o2.getPriority();
            if (v1 > v2) {
                return 1;
            } else if (v1 < v2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
