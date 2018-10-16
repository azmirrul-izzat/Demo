package com.apddemo.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GenericUtils {

    private static Logger LOG = LoggerFactory.getLogger(GenericUtils.class);

    public static void logQuery(Logger log, Map<String, String> queryMap) {
        StringBuilder queryBuilderDebug = new StringBuilder(System.lineSeparator());
        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            queryBuilderDebug.append(entry.getKey()).append("=").append(entry.getValue()).append(System.lineSeparator());
        }
        log.info(queryBuilderDebug.toString());
    }

}
