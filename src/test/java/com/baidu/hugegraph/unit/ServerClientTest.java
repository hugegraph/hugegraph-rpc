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

import com.alipay.sofa.rpc.common.RpcOptions;
import com.baidu.hugegraph.rpc.RpcClientProvider;
import com.baidu.hugegraph.rpc.RpcCommonConfig;
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
    public void testSimpleService() {
        RpcProviderConfig serverConfig = rpcServer.config();
        serverConfig.addService(HelloService.class, new HelloServiceImpl());
        starServer(rpcServer);

        RpcConsumerConfig clientConfig = rpcClient.config();
        HelloService client = clientConfig.serviceProxy(HelloService.class);

        Assert.assertEquals("hello tom!", client.hello("tom"));
        Assert.assertEquals("tom", client.echo("tom"));
        Assert.assertEquals(5.14, client.sum(2, 3.14), 0.00000001d);
    }

    @Test
    public void testMultiService() {
        GraphHelloServiceImpl g1 = new GraphHelloServiceImpl("g1");
        GraphHelloServiceImpl g2 = new GraphHelloServiceImpl("g2");
        GraphHelloServiceImpl g3 = new GraphHelloServiceImpl("g3");

        RpcProviderConfig serverConfig = rpcServer.config();
        serverConfig.addService(g1.graph(), HelloService.class, g1);
        serverConfig.addService(g2.graph(), HelloService.class, g2);
        serverConfig.addService(g3.graph(), HelloService.class, g3);
        starServer(rpcServer);

        RpcConsumerConfig clientConfig = rpcClient.config();
        HelloService c1 = clientConfig.serviceProxy("g1", HelloService.class);
        HelloService c2 = clientConfig.serviceProxy("g2", HelloService.class);
        HelloService c3 = clientConfig.serviceProxy("g3", HelloService.class);

        Assert.assertEquals("g1: hello tom!", c1.hello("tom"));
        Assert.assertEquals("g1: tom", c1.echo("tom"));
        Assert.assertEquals(5.14, c1.sum(2, 3.14), 0.00000001d);

        Assert.assertEquals("g2: hello tom!", c2.hello("tom"));
        Assert.assertEquals("g2: tom", c2.echo("tom"));
        Assert.assertEquals(6.14, c2.sum(3, 3.14), 0.00000001d);

        Assert.assertEquals("g3: hello tom!", c3.hello("tom"));
        Assert.assertEquals("g3: tom", c3.echo("tom"));
        Assert.assertEquals(103.14, c3.sum(100, 3.14), 0.00000001d);

        Assert.assertEquals(5.14, g1.result(), 0.00000001d);
        Assert.assertEquals(6.14, g2.result(), 0.00000001d);
        Assert.assertEquals(103.14, g3.result(), 0.00000001d);
    }

    @Test
    public void testStartBothServerAndClient() {

    }

    @Test
    public void testLoadBalancer() {
        RpcCommonConfig.initRpcConfigs(RpcOptions.CONSUMER_LOAD_BALANCER,
                                       "random");
    }

    @Test
    public void testExportNoneService() {

    }

    @Test
    public void testUnExportService() {

    }

    @Test
    public void testRpcFanoutService() {

    }

    public static interface HelloService {

        public String hello(String string);

        public String echo(String string);

        public double sum(long a, double b);
    }

    public static class HelloServiceImpl implements HelloService {

        @Override
        public String hello(String string) {
            return "hello " + string + "!";
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

    public static class GraphHelloServiceImpl implements HelloService {

        private final String graph;
        private double result;

        public GraphHelloServiceImpl(String graph) {
            this.graph = graph;
        }

        public String graph() {
            return this.graph;
        }

        public double result() {
            return this.result;
        }

        @Override
        public String hello(String string) {
            return this.graph + ": hello " + string + "!";
        }

        @Override
        public String echo(String string) {
            return  this.graph + ": " + string;
        }

        @Override
        public double sum(long a, double b) {
            this.result = a + b;
            return this.result;
        }
    }
}
