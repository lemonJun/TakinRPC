package com.lemon.soa.moduler;

import com.lemon.soa.Adaptor;

public interface IModuler<REQ, RES> extends Adaptor {

    void notify(Moduler<REQ, RES> moduler);

}
