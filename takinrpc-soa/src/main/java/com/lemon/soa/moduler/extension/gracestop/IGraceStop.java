package com.lemon.soa.moduler.extension.gracestop;

import com.lemon.soa.moduler.IModuler;

public interface IGraceStop<REQ, RES> extends IModuler<REQ, RES> {

    RES gracestop(REQ req, IGraceStopProcessor<REQ, RES> processor, Object... args);

}
