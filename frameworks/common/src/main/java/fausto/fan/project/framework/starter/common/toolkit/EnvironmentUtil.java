/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fausto.fan.project.framework.starter.common.toolkit;

import fausto.fan.project.framework.starter.base.ApplicationContextHolder;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * 环境工具类
 */
public class EnvironmentUtil {

    private static List<String> ENVIRONMENT_LIST = new ArrayList<>();

    static {
        ENVIRONMENT_LIST.add("dev");
        ENVIRONMENT_LIST.add("test");
    }

    /**
     * 判断当前是否为开发环境
     *
     * @return
     */
    public static boolean isDevEnvironment() {
        ConfigurableEnvironment configurableEnvironment = ApplicationContextHolder.INSTANCE.getBean(null, ConfigurableEnvironment.class);
        String propertyActive = configurableEnvironment.getProperty("spring.profiles.active", "dev");
        return ENVIRONMENT_LIST.stream().anyMatch(propertyActive::contains);
    }

    /**
     * 判断当前是否为正式环境
     *
     * @return
     */
    public static boolean isProdEnvironment() {
        ConfigurableEnvironment configurableEnvironment = ApplicationContextHolder.INSTANCE.getBean(null, ConfigurableEnvironment.class);
        String propertyActive = configurableEnvironment.getProperty("spring.profiles.active", "dev");
        return ENVIRONMENT_LIST.stream().noneMatch(propertyActive::contains);
    }
}
