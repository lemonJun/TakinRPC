package com.lemon.soa.type;

/**
 * The Adaptor Type.
 * 
 * me
 */
public interface ITypeAdaptor extends IMsgAdaptor {

    /**
     * Getter filter value.
     * 
     * @return
     */
    String getVal();

    /**
     * Setter filter value.
     * 
     * @param value
     */
    void setVal(String value);

}
