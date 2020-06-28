package com.example.es_demo.service.impl;

import com.example.es_demo.VO.EsFileVo;
import com.example.es_demo.VO.ItemForm;
import com.example.es_demo.constant.FiledConst;
import com.example.es_demo.service.esService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("esService")
@Slf4j
public class esServiceImpl implements esService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<EsFileVo> queryItem(ItemForm itemForm) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(itemForm.getKeyword())){
            for (String value : FiledConst.filedMap.values()) {
                boolQueryBuilder.should(QueryBuilders.wildcardQuery(value + ".keyword", "*" + itemForm.getKeyword() + "*"));
            }
        }

        SearchResponse searchResponse = this.search(itemForm.getTable(), boolQueryBuilder, itemForm.getPage(), itemForm.getSize());
        SearchHits searchHits = searchResponse.getHits();
        Long total = searchHits.getTotalHits().value;
        List<EsFileVo> sourceList = Arrays.stream(searchHits.getHits()).map(v->{
            EsFileVo esFileVo = new EsFileVo();

            esFileVo.setId(Integer.parseInt(v.getId()));
            esFileVo.setTitle(String.valueOf(v.getSourceAsMap().get(FiledConst.filedMap.get(1))));
            esFileVo.setContent(String.valueOf(v.getSourceAsMap().get(FiledConst.filedMap.get(2))));
            esFileVo.setFilePath(String.valueOf(v.getSourceAsMap().get(FiledConst.filedMap.get(3))));
            esFileVo.setCreateDate(String.valueOf(v.getSourceAsMap().get(FiledConst.filedMap.get(4))));
            // 反射调用set方法将高亮内容设置进去
            if (v.getHighlightFields() != null && v.getHighlightFields().size() != 0) {

                for (int i = 1; i <= v.getHighlightFields().size(); i ++) {
                    for (String value : FiledConst.filedMap.values()) {
                        if (v.getHighlightFields().get(value) != null) {
                            String highLightMessage = v.getHighlightFields().get(value).fragments()[0].toString();
                            try {

                                String setMethodName = parSetName(value);
                                Class<? extends EsFileVo> poemClazz = esFileVo.getClass();
                                Method setMethod = poemClazz.getMethod(setMethodName, String.class);
                                setMethod.invoke(esFileVo, highLightMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return esFileVo;
        }).collect(Collectors.toList());
        return sourceList;
    }

    private SearchResponse search(String tbNm, BoolQueryBuilder boolQueryBuilder, Integer page, Integer size){
        System.out.println("查询的语句:" + boolQueryBuilder.toString());
        try {
            return restHighLevelClient.search(new SearchRequest(tbNm)
                            .source(new SearchSourceBuilder()
                                    .size(size)
                                    .from((page - 1) * size)
                                    .query(boolQueryBuilder)
                                    .highlighter(getHighlightBuilder())
                            ),
                    RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("searchResponse异常:{}", e);
        }
        return new SearchResponse();
    }

    private HighlightBuilder getHighlightBuilder () {

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder
                .requireFieldMatch(false)
                .preTags("<span style='color: red'>")
                .postTags("<span>");
        for (String value : FiledConst.filedMap.values()) {
            highlightBuilder.field(value);
        }
        return highlightBuilder;
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }
}
