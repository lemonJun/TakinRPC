/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.takin.rpc.registry;

import java.util.List;

import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkNoNodeException;
import com.github.zkclient.exception.ZkNodeExistsException;
import com.takin.emmet.io.NIOUtils;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 1.0
 */
public class ZkUtils {

    public static final String ConsumersPath = "/consumers";

    public static final String BrokerIdsPath = "/brokers/ids";

    public static final String BrokerTopicsPath = "/brokers/topics";

    public static void makeSurePersistentPathExists(ZkClient zkClient, String path) {
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path, true);
        }
    }

    /**
     * get children nodes name
     *
     * @param zkClient zkClient
     * @param path     full path
     * @return children nodes name or null while path not exist
     */
    public static List<String> getChildrenParentMayNotExist(ZkClient zkClient, String path) {
        try {
            return zkClient.getChildren(path);
        } catch (ZkNoNodeException e) {
            return null;
        }
    }

    public static String readData(ZkClient zkClient, String path) {
        return NIOUtils.fromBytes(zkClient.readData(path));
    }

    public static String readDataMaybeNull(ZkClient zkClient, String path) {
        return NIOUtils.fromBytes(zkClient.readData(path, true));
    }

    public static void updatePersistentPath(ZkClient zkClient, String path, String data) {
        try {
            zkClient.writeData(path, NIOUtils.getBytes(data));
        } catch (ZkNoNodeException e) {
            createParentPath(zkClient, path);
            try {
                zkClient.createPersistent(path, NIOUtils.getBytes(data));
            } catch (ZkNodeExistsException e2) {
                zkClient.writeData(path, NIOUtils.getBytes(data));
            }
        }
    }

    private static void createParentPath(ZkClient zkClient, String path) {
        String parentDir = path.substring(0, path.lastIndexOf('/'));
        if (parentDir.length() != 0) {
            zkClient.createPersistent(parentDir, true);
        }
    }

    public static void deletePath(ZkClient zkClient, String path) {
        try {
            zkClient.delete(path);
        } catch (ZkNoNodeException e) {
        }
    }

    public static String readDataMaybyNull(ZkClient zkClient, String path) {
        return NIOUtils.fromBytes(zkClient.readData(path, true));
    }

    /**
     * 创建一个临时节点
     * Create an ephemeral node with the given path and data. Create parents if necessary.
     * @param zkClient client of zookeeper
     * @param path node path of zookeeper
     * @param data node data
     */
    public static void createEphemeralPath(ZkClient zkClient, String path, String data) {
        try {
            zkClient.createEphemeral(path, NIOUtils.getBytes(data));
        } catch (ZkNoNodeException e) {
            createParentPath(zkClient, path);
            zkClient.createEphemeral(path, NIOUtils.getBytes(data));
        }
    }

    public static void createEphemeralPathExpectConflict(ZkClient zkClient, String path, String data) {
        try {
            createEphemeralPath(zkClient, path, data);
        } catch (ZkNodeExistsException e) {
            //this can happend when there is connection loss;
            //make sure the data is what we intend to write
            String storedData = null;
            try {
                storedData = readData(zkClient, path);
            } catch (ZkNoNodeException e2) {
                //ignore
            }
            if (storedData == null || !storedData.equals(data)) {
                throw new ZkNodeExistsException("conflict in " + path + " data: " + data + " stored data: " + storedData);
            }
            //
            //otherwise, the creation succeeded, return normally
        }
    }
}
