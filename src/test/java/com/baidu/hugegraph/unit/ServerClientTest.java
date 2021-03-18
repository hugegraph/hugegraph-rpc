/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.unit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.baidu.hugegraph.rpc.RpcClientProvider;
import com.baidu.hugegraph.rpc.RpcConsumerConfig;
import com.baidu.hugegraph.rpc.RpcProviderConfig;
import com.baidu.hugegraph.rpc.RpcServer;
import com.baidu.hugegraph.testutil.Assert;

public class ServerClientTest extends BaseUnitTest {

    private static RpcServer rpcServer;
    private static RpcClientProvider rpcClient;

    @BeforeClass
    public static void init() {
        rpcServer = new RpcServer(config(true));
        rpcClient = new RpcClientProvider(config(false));
    }

    @AfterClass
    public static void clear() throws Exception {
        if (rpcClient != null) {
            rpcClient.destroy();
        }
        if (rpcServer != null) {
            rpcServer.destroy();
        }
    }

    @Test
    public void testRpcSimpleService() {
        RpcProviderConfig serverConfig = rpcServer.config();
        serverConfig.addService(HelloService.class, new HelloServiceImpl());
        rpcServer.exportAll();

        RpcConsumerConfig clientConfig = rpcClient.config();
        HelloService client = clientConfig.serviceProxy(HelloService.class);

        Assert.assertEquals("tom world!", client.hello("tom"));
        Assert.assertEquals("tom", client.echo("tom"));
        Assert.assertEquals(5.14, client.sum(2, 3.14), 0.00000001d);
    }

    public static interface HelloService {

        public String hello(String string);

        public String echo(String string);

        public double sum(long a, double b);
    }

    public static class HelloServiceImpl implements HelloService {

        @Override
        public String hello(String string) {
            return string + " world!";
        }

        @Override
        public String echo(String string) {
            return string;
        }

        @Override
        public double sum(long a, double b) {
            return a + b;
        }
    }
}
