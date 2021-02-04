package com.jgxq.admin.client;

import com.alibaba.fastjson.JSONObject;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MouFangCai
 * @date 2019/12/9 10:52
 * @description
 */
@Component
public class EsUtils {

    @Autowired
    private RestHighLevelClient esClient;

    private String esType = "_doc";

    /**
     * 搜索
     * @param index
     * @param searchSourceBuilder
     * @param clazz
     * @param pageNum
     * @param pageSize
     * @return PageResponse<T>
     */
    public  <T> PageResponse<T> search(String index, SearchSourceBuilder searchSourceBuilder, Class<T> clazz, Integer pageNum, Integer pageSize){
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(esType);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        PageResponse<T> pageResponse = null;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            pageResponse = new PageResponse<>();
            pageResponse.setPage(pageNum);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotal(response.getHits().getTotalHits().value);
            List<T> dataList = new ArrayList<>();
            SearchHits hits = response.getHits();
            for(SearchHit hit : hits){
                dataList.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
            }
            pageResponse.setData(dataList);
        } catch (Exception e) {
            throw new SmartException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute searching,because of " + e.getMessage());
        }
        return pageResponse;
    }

    public  <T> List<T> search(String index, SearchSourceBuilder searchSourceBuilder, Class<T> clazz, Integer pageSize){
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(esType);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = null;
        List<T> dataList = null;
        try {
            response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(searchRequest.source().toString());
            dataList = new ArrayList<>();
            SearchHits hits = response.getHits();
            for(SearchHit hit : hits){
                dataList.add(JSONObject.parseObject(hit.getSourceAsString(), clazz));
            }
        } catch (Exception e) {
            throw new SmartException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute searching,because of " + e.getMessage());
        }
        return dataList;
    }

    /**
     * 聚合
     * @param index
     * @param searchSourceBuilder
     * @param aggName 聚合名
     * @param pageNum
     * @param pageSize
     * @return Map<Integer, Long>  key:aggName   value: doc_count
     */
    public Map<Integer, Long> search(String index, SearchSourceBuilder searchSourceBuilder,
                                     String aggName, Integer pageNum, Integer pageSize){
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(esType);
        searchRequest.source(searchSourceBuilder);
        Map<Integer, Long> responseMap = new HashMap<>(0);
        try {
            SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get(aggName);
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            responseMap = new HashMap<>(buckets.size());
            Map<Integer, Long> finalResponseMap = responseMap;
            buckets.forEach(bucket-> {
                finalResponseMap.put(bucket.getKeyAsNumber().intValue(), bucket.getDocCount());
            });
        } catch (Exception e) {
            throw new SmartException(String.valueOf(HttpStatus.BAD_REQUEST), "error to execute searching Bucket,because of " + e.getMessage());
        }
        return responseMap;
    }


    /**
     * 将搜索关键词中的 非中文 替换为 *
     *
     * @param searchStr
     * @return
     */
    public  String searchStrToWildcard(String searchStr) {
        // 使用正则表达式 [\u4E00-\u9FA5]是unicode2的中文区间
        Pattern pattern = Pattern.compile("[^\u4E00-\u9FA5]");
        Matcher matcher = pattern.matcher(searchStr);
        String invalidStr = matcher.replaceAll("");
        if (StringUtils.isEmpty(invalidStr)) {
            return null;
        }
       return "*" + matcher.replaceAll("*") + "*";
    }

}
