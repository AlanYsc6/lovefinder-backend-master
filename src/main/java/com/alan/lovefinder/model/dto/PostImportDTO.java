package com.alan.lovefinder.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;

import java.io.Serializable;

import lombok.Data;

/**
 * 帖子 Excel 导入封装类
 *
 * @author alan
 */
@Data
public class PostImportDTO implements Serializable {

    /**
     * 年龄
     */
    @ExcelProperty("年龄")
    private Integer age;

    /**
     * 性别（0-男, 1-女）
     */
    @ExcelProperty("性别")
    private Integer gender;


    /**
     * 学历
     */
    @ExcelProperty("学历")
    private String education;

    /**
     * 地点
     */
    @ExcelProperty("地点")
    private String place;

    /**
     * 职业
     */
    @ExcelProperty("职业")
    private String job;

    /**
     * 联系方式
     */
    @ExcelProperty("联系方式")
    private String contact;

    /**
     * 感情经历
     */
    @ExcelProperty("感情经历")
    private String loveExp;

    /**
     * 内容（个人介绍）
     */
    @ExcelProperty("个人介绍")
    private String content;

    /**
     * 照片地址
     */
    @ExcelProperty("照片地址")
    private String photo;

}