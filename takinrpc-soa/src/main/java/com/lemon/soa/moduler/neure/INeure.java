package com.lemon.soa.moduler.neure;

import com.lemon.soa.common.exception.neure.NeureException;
import com.lemon.soa.moduler.IModuler;

public interface INeure<REQ, RES> extends IModuler<REQ, RES> {

    RES neure(REQ req, INeureProcessor<REQ, RES> processor, Object... args) throws NeureException;

}
