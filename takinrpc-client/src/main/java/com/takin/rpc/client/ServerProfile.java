package com.takin.rpc.client;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * ServerProfile
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public class ServerProfile {

    private String name;
    private String host;
    private int port;
    private int deadTimeout;
    private float weithtRate;

    public ServerProfile(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        this.name = attributes.getNamedItem("name").getNodeValue();
        this.host = attributes.getNamedItem("host").getNodeValue();
        this.port = Integer.parseInt(attributes.getNamedItem("port").getNodeValue());
        Node atribute = attributes.getNamedItem("weithtRate");
        if (atribute != null) {
            this.weithtRate = Float.parseFloat(atribute.getNodeValue().toString());
        } else {
            this.weithtRate = 1;
        }
        atribute = node.getParentNode().getAttributes().getNamedItem("deadTimeout");
        this.deadTimeout = 30;

    }

    /*
     * Unit is ms
     */
    public int getDeadTimeout() {
        return deadTimeout;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public float getWeithtRate() {
        return weithtRate;
    }
}
