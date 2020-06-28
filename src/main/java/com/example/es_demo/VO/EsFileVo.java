package com.example.es_demo.VO;

import lombok.Data;

import java.util.Date;

@Data
public class EsFileVo {

    private Integer id;

    private String title;

    private String content;

    private String filePath;

    private String createDate;

}
