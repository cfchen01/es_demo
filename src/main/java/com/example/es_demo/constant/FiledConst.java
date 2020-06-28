package com.example.es_demo.constant;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FiledConst {

    public static Map<Integer, String> filedMap = new HashMap<>();

    {
        filedMap.put(1, "title");
        filedMap.put(2, "content");
        filedMap.put(3, "filePath");
        filedMap.put(4, "createDate");
    }
}
