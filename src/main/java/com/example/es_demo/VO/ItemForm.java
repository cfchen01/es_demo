package com.example.es_demo.VO;

import lombok.Data;

@Data
public class ItemForm {

    private String table;

    private String keyword;

    private Integer page;

    private Integer size;
}
