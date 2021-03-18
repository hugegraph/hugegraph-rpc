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

import java.net.URL;

import org.junit.BeforeClass;

import com.baidu.hugegraph.config.HugeConfig;
import com.baidu.hugegraph.config.OptionSpace;

public class BaseUnitTest {

    @BeforeClass
    public static void initEnv() {
        OptionSpace.register("rpc", "com.baidu.hugegraph.config.RpcOptions");
    }

    protected static HugeConfig config(boolean server) {
        String name = String.format("rpc-%s.properties",
                                    server ? "server" : "client");
        URL conf = BaseUnitTest.class.getClassLoader().getResource(name);
        return new HugeConfig(conf.getPath());
    }
}
