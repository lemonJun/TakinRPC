/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.takin.rpc.server;

import javax.inject.Singleton;

import com.google.inject.Inject;

/**

 *
 * @author lemon
 * 
 */
@Singleton
public class NettyServerConfig {

    //
    private String servername = "test";
    private int listenPort = 6871;

    private boolean usezk = false;
    private String zkhosts = "";

    private int workerThreads = 32;
    private int callbackExecutorThreads = 1;
    private int selectorThreads = 4;
    private int onewaySemaphoreValue = 256;
    private int asyncSemaphoreValue = 64;
    private int channelMaxIdleTimeSeconds = 120;

    private int socketSndBufSize = 4096;
    private int socketRcvBufSize = 4096;
    private boolean pooledByteBufAllocatorEnable = true;

    private boolean useEpollNativeSelector = true;

    @Inject
    private NettyServerConfig() {
    }

    public String getUUID() {
        return String.format("%s_%d", servername, listenPort);
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public boolean isUsezk() {
        return usezk;
    }

    public void setUsezk(boolean usezk) {
        this.usezk = usezk;
    }

    public String getZkhosts() {
        return zkhosts;
    }

    public void setZkhosts(String zkhosts) {
        this.zkhosts = zkhosts;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public int getCallbackExecutorThreads() {
        return callbackExecutorThreads;
    }

    public void setCallbackExecutorThreads(int callbackExecutorThreads) {
        this.callbackExecutorThreads = callbackExecutorThreads;
    }

    public int getSelectorThreads() {
        return selectorThreads;
    }

    public void setSelectorThreads(int selectorThreads) {
        this.selectorThreads = selectorThreads;
    }

    public int getOnewaySemaphoreValue() {
        return onewaySemaphoreValue;
    }

    public void setOnewaySemaphoreValue(int onewaySemaphoreValue) {
        this.onewaySemaphoreValue = onewaySemaphoreValue;
    }

    public int getAsyncSemaphoreValue() {
        return asyncSemaphoreValue;
    }

    public void setAsyncSemaphoreValue(int asyncSemaphoreValue) {
        this.asyncSemaphoreValue = asyncSemaphoreValue;
    }

    public int getChannelMaxIdleTimeSeconds() {
        return channelMaxIdleTimeSeconds;
    }

    public void setChannelMaxIdleTimeSeconds(int channelMaxIdleTimeSeconds) {
        this.channelMaxIdleTimeSeconds = channelMaxIdleTimeSeconds;
    }

    public int getSocketSndBufSize() {
        return socketSndBufSize;
    }

    public void setSocketSndBufSize(int socketSndBufSize) {
        this.socketSndBufSize = socketSndBufSize;
    }

    public int getSocketRcvBufSize() {
        return socketRcvBufSize;
    }

    public void setSocketRcvBufSize(int socketRcvBufSize) {
        this.socketRcvBufSize = socketRcvBufSize;
    }

    public boolean isPooledByteBufAllocatorEnable() {
        return pooledByteBufAllocatorEnable;
    }

    public void setPooledByteBufAllocatorEnable(boolean pooledByteBufAllocatorEnable) {
        this.pooledByteBufAllocatorEnable = pooledByteBufAllocatorEnable;
    }

    public boolean isUseEpollNativeSelector() {
        return useEpollNativeSelector;
    }

    public void setUseEpollNativeSelector(boolean useEpollNativeSelector) {
        this.useEpollNativeSelector = useEpollNativeSelector;
    }

}
