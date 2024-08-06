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

package fausto.fan.project.framework.starter.database.algorithm.sharding;

import org.apache.shardingsphere.infra.util.exception.ShardingSpherePreconditions;
import org.apache.shardingsphere.sharding.algorithm.sharding.ShardingAutoTableAlgorithmUtil;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.apache.shardingsphere.sharding.exception.algorithm.sharding.ShardingAlgorithmInitializationException;

import java.util.Collection;
import java.util.Properties;

/**
 * Custom db hash sharding algorithm.
 */
/**
 * 自定义数据库哈希模组分片算法实现类
 * 该类基于哈希值和配置的分片数量进行数据分片
 */
public final class CustomDbHashModShardingAlgorithm implements StandardShardingAlgorithm<Comparable<?>> {

    // 分片数量配置项键
    private static final String SHARDING_COUNT_KEY = "sharding-count";
    // 表分片数量配置项键
    private static final String TABLE_SHARDING_COUNT_KEY = "table-sharding-count";

    // 分片数量
    private int shardingCount;
    // 表分片数量
    private int tableShardingCount;

    /**
     * 初始化方法，用于从属性集合中读取并设置分片数量和表分片数量
     *
     * @param props 属性集合，包含分片规则相关的配置项
     */
    @Override
    public void init(final Properties props) {
        shardingCount = getShardingCount(props);
        tableShardingCount = getTableShardingCount(props);
    }

    /**
     * 执行精确分片方法，根据分片值计算出对应的分片名称
     *
     * @param availableTargetNames 可用的目标名称集合
     * @param shardingValue 分片值，包含具体的分片键值和分片算法需要的信息
     * @return 分片名称，如果匹配则返回对应的名称，否则返回null
     */
    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Comparable<?>> shardingValue) {
        // 计算分片值的哈希码，并根据分片数量和表分片数量计算出分片后缀
        String suffix = String.valueOf(hashShardingValue(shardingValue.getValue()) % shardingCount / tableShardingCount);
        // 查找匹配的目标名称
        return ShardingAutoTableAlgorithmUtil.findMatchedTargetName(availableTargetNames, suffix, shardingValue.getDataNodeInfo()).orElse(null);
    }

    /**
     * 执行范围分片方法，目前简单返回所有可用目标名称
     *
     * @param availableTargetNames 可用的目标名称集合
     * @param shardingValue 分片值，包含具体的分片键值和分片算法需要的信息
     * @return 所有可用目标名称的集合
     */
    @Override
    public Collection<String> doSharding(final Collection<String> availableTargetNames, final RangeShardingValue<Comparable<?>> shardingValue) {
        return availableTargetNames;
    }

    /**
     * 从属性集合中获取分片数量
     *
     * @param props 属性集合，包含分片规则相关的配置项
     * @return 分片数量
     * @throws ShardingAlgorithmInitializationException 如果未找到配置项，则抛出初始化异常
     */
    private int getShardingCount(final Properties props) {
        ShardingSpherePreconditions.checkState(props.containsKey(SHARDING_COUNT_KEY), () -> new ShardingAlgorithmInitializationException(getType(), "Sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(SHARDING_COUNT_KEY));
    }

    /**
     * 从属性集合中获取表分片数量
     *
     * @param props 属性集合，包含分片规则相关的配置项
     * @return 表分片数量
     * @throws ShardingAlgorithmInitializationException 如果未找到配置项，则抛出初始化异常
     */
    private int getTableShardingCount(final Properties props) {
        ShardingSpherePreconditions.checkState(props.containsKey(TABLE_SHARDING_COUNT_KEY), () -> new ShardingAlgorithmInitializationException(getType(), "Table sharding count cannot be null."));
        return Integer.parseInt(props.getProperty(TABLE_SHARDING_COUNT_KEY));
    }

    /**
     * 计算分片值的哈希码，用于分片计算
     *
     * @param shardingValue 分片值对象
     * @return 分片值的哈希码
     */
    private long hashShardingValue(final Object shardingValue) {
        return Math.abs((long) shardingValue.hashCode());
    }

    /**
     * 获取分片算法类型标识
     *
     * @return 分片算法类型标识
     */
    @Override
    public String getType() {
        return "CLASS_BASED";
    }
}
