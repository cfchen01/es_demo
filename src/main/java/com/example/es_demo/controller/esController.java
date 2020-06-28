package com.example.es_demo.controller;

import com.example.es_demo.VO.EsFileVo;
import com.example.es_demo.VO.ItemForm;
import com.example.es_demo.VO.ResultVO;
import com.example.es_demo.service.esService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class esController {

    @Autowired
    private esService esService;

    @PostMapping("/queryItem")
    public ResultVO queryItem (@RequestBody ItemForm itemForm) {

        List<EsFileVo> list = esService.queryItem(itemForm);
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(0);
        resultVO.setData(list);

        return  resultVO;
    }
}
