/*-
 * <<
 * DBus
 * ==
 * Copyright (C) 2016 - 2018 Bridata
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */

package com.creditease.dbus.stream.common.appender.utils;

import com.creditease.dbus.stream.common.Constants;
import com.creditease.dbus.stream.common.appender.cache.GlobalCache;
import com.creditease.dbus.stream.common.appender.enums.Command;
import com.creditease.dbus.stream.common.appender.bean.DbusDatasource;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.apache.storm.shade.org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 公用的Utils类,可以提供一般的公用方法
 * Created by Shrimp on 16/5/25.
 */
public final class Utils {
    /**
     * 获取数据源信息,服务启动时保存数据源到缓存中
     */
    public static DbusDatasource getDatasource() {
        return GlobalCache.getDatasource();
    }

    public static String getDataSourceNamespace() {
        DbusDatasource ds = getDatasource();
        return join(".", ds.getDsType(), ds.getDsName());
    }

    /**
     * 通过connector将parts中的字符串连接起来
     *
     * @param connector
     * @param parts     要连接起来的字符串列表
     * @return 返回连接后的字符串
     */
    public static String join(String connector, String... parts) {
        return Joiner.on(connector).join(parts);
    }

    public static String replaceBlanks(String data) {
        return data.replaceAll("\\s", "");
    }

    /**
     * 构造Avro Schema名称
     *
     * @param hash schema文件的hash code
     * @param args 构造参数数组
     * @return Avro Schema名称 xxx.xxx_hash
     */
    public static String buildAvroSchemaName(int hash, String... args) {
        return join(".", join(".", args), hash + "");
    }

    /**
     * 构造DataTable对象存储到cache 中的key值
     *
     * @param schema
     * @return table
     */
    public static String buildDataTableCacheKey(String schema, String table) {
        return join(".", schema, table);
    }

    /**
     * 生成默认的输出topic,即dbus-ora-dispatcher的输出topic,实际上是dbus-ora-appender的输入topic
     *
     * @param ds     数据源
     * @param schema 表的schema
     * @return
     */
    public static String defaultOutputTopic(String ds, String schema) {
        return join(".", ds, schema);
    }

    /**
     * 生成dbus-ora-appender结果的默认topic
     *
     * @param ds     数据源
     * @param schema 表的schema
     * @return
     */
    public static String defaultResultTopic(String ds, String schema) {
        return join(".", ds, schema, "result");
    }

    public static String buildZKTopologyPath(String topologyId) {
        return Constants.ZKPath.ZK_TOPOLOGY_ROOT + "/" + topologyId;
    }

    /**
     * 将ISO Date Time 字符串转换成java.util.Date对象
     *
     * @param isoDateTime yyyy-MM-ddTHH:mm:ss
     * @return
     */
    public static Date isoDateTimeDdate(String isoDateTime) {
        DateTime dateTime = ISODateTimeFormat.dateParser().parseDateTime(isoDateTime);
        return dateTime.toDate();
    }

    /**
     * 判断是否为特殊命令 key的格式:schema.table.schema_hash
     */
    public static Command parseCommand(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return Command.UNKNOWN_CMD;
        }

        int lastIdx = key.lastIndexOf(".");
        if (lastIdx <= 0) {
            return Command.parse(key);
        }
        return Command.parse(key.substring(0, lastIdx));
    }

    public static long getTimeMills(String timeStr) throws ParseException {
        String ptn = "yyyy-MM-dd HH:mm:ss.SSS";
        timeStr = timeStr.substring(0, 23);
        if (timeStr.length() == 19) {
            ptn = "yyyy-MM-dd HH:mm:ss";
        }

        DateFormat df = new SimpleDateFormat(ptn);
        return df.parse(timeStr).getTime();
    }
}
