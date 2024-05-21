package com.alan.lovefinder.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签分类枚举
 *
 * @author alan
 */
public enum TagCategoryEnum {

    EDUCATION("学历"),
    PLACE("地点"),
    JOB("职业"),
    LOVE_EXP("感情经历");

    private final String value;

    TagCategoryEnum(String value) {
        this.value = value;
    }

    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getValue() {
        return value;
    }
}
