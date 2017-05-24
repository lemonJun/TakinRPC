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
package com.takin.rpc.client;

/**
 * @author lemon
 *
 */
public class NettyClientConfig {
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
    private int callbackExecutorThreads = 2;
    
    private int onewaySemaphoreValue = 64;
    private int asyncSemaphoreValue = 64;
    private int connectTimeoutMillis = 3000;
    private long channelNotActiveInterval = 1000 * 60;

    /**
     * IdleStateEvent will be triggered when neither read nor write was performed for
     * the specified period of this time. Specify {@code 0} to disable
     */
    private int channelMaxIdleTimeSeconds = 120;

    private int sndBufSize = 4096;
    private int socketRcvBufSize = 4096;
    private boolean pooledByteBufAllocatorEnable = false;
    private boolean closeSocketIfTimeout = false;

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

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public long getChannelNotActiveInterval() {
        return channelNotActiveInterval;
    }

    public void setChannelNotActiveInterval(long channelNotActiveInterval) {
        this.channelNotActiveInterval = channelNotActiveInterval;
    }

    public int getChannelMaxIdleTimeSeconds() {
        return channelMaxIdleTimeSeconds;
    }

    public void setChannelMaxIdleTimeSeconds(int channelMaxIdleTimeSeconds) {
        this.channelMaxIdleTimeSeconds = channelMaxIdleTimeSeconds;
    }

    public int getSndBufSize() {
        return sndBufSize;
    }

    public void setSndBufSize(int sndBufSize) {
        this.sndBufSize = sndBufSize;
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

    public boolean isCloseSocketIfTimeout() {
        return closeSocketIfTimeout;
    }

    public void setCloseSocketIfTimeout(boolean closeSocketIfTimeout) {
        this.closeSocketIfTimeout = closeSocketIfTimeout;
    }

}
