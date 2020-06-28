package com.example.es_demo.service;

import com.example.es_demo.VO.EsFileVo;
import com.example.es_demo.VO.ItemForm;

import java.util.List;


public interface esService {

    List<EsFileVo> queryItem(ItemForm itemForm);
}
