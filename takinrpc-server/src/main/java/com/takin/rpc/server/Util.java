package com.takin.rpc.server;

import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * get simple para name
     * @param paraName
     * @return
     */
    public static String getSimpleParaName(String paraName) {
        paraName = paraName.replaceAll(" ", "");
        if (paraName.indexOf(".") > 0) {
            String[] pnAry = paraName.split("");
            List<String> originalityList = new ArrayList<String>();
            List<String> replaceList = new ArrayList<String>();

            String tempP = "";
            for (int i = 0; i < pnAry.length; i++) {
                if (pnAry[i].equalsIgnoreCase("<")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (pnAry[i].equalsIgnoreCase(">")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (pnAry[i].equalsIgnoreCase(",")) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else if (i == pnAry.length - 1) {
                    originalityList.add(tempP);
                    replaceList.add(tempP.substring(tempP.lastIndexOf(".") + 1));
                    tempP = "";
                } else {
                    if (!pnAry[i].equalsIgnoreCase("[") && !pnAry[i].equalsIgnoreCase("]")) {
                        tempP += pnAry[i];
                    }
                }
            }

            for (int i = 0; i < replaceList.size(); i++) {
                paraName = paraName.replaceAll(originalityList.get(i), replaceList.get(i));
            }
            return paraName;
        } else {
            return paraName;
        }
    }
}
