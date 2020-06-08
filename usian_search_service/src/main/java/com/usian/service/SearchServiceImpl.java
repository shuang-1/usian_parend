package com.usian.service;


import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import com.usian.utils.JsonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentType.JSON;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;


    @Override
    public boolean importAll() {

        try {

            if(!isExistsIndex()){
                createIndex();
            }
            int page = 1;
            while (true) {
                PageHelper.startPage(page, 1000);
                List<SearchItem> itemList = searchItemMapper.getItemList();
                if(itemList.size()==0 || itemList==null){
                    break;
                }
                BulkRequest bulkRequest = new BulkRequest();
                for (int i = 0; i < itemList.size(); i++) {
                    SearchItem searchItem = itemList.get(i);
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME).source
                            (JsonUtils.objectToJson(searchItem), JSON));
                }
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                page++;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断索引是否存在
    public boolean isExistsIndex() throws IOException {
        GetIndexRequest indexRequest = new GetIndexRequest();
        indexRequest.indices(ES_INDEX_NAME);
        return restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
    }
    //创建索引库
    private void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
        createIndexRequest.settings(Settings.builder().put("number_of_shards",2).put("number_of_replicas",1));
        createIndexRequest.mapping(ES_TYPE_NAME,"{\n" +
                "  \"_source\": {\n" +
                "    \"excludes\":[\"item_desc\"]\n" +
                "  }, \n" +
                "  \"properties\": {\n" +
                "    \"itme_title\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"id\":{\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"item_sell_point\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_price\":{\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"item_image\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"item_category_name\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"itme_desc\":{\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "}", XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest,RequestOptions.DEFAULT);
    }

    @Override
    public List<SearchItem> selectByq(String q, Long page, Integer pagesize) {
        try {
            SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME).types(ES_TYPE_NAME);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q,"item_title","item_sell_point","item_desc","item_category_name"));
            //分页
            Long from = (page-1)*pagesize;
            searchSourceBuilder.from(from.intValue());
            searchSourceBuilder.size(pagesize);
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //高亮
            highlightBuilder.preTags("<font color='red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("item_title");
            searchSourceBuilder.highlighter(highlightBuilder);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<SearchItem> items = new ArrayList<>();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                String sourceAsString = hit.getSourceAsString();
                SearchItem searchItem = JsonUtils.jsonToPojo(sourceAsString, SearchItem.class);

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(highlightFields.get("item_title")!=null){
                    searchItem.setItem_title(highlightFields.get("item_title").getFragments()[0].toString());
                }
                items.add(searchItem);
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int addDocement(String msg) {

        SearchItem searchItem = searchItemMapper.getItemById(msg);
        IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME);

        indexRequest.source(JsonUtils.objectToJson(searchItem), XContentType.JSON);

        IndexResponse indexResponse = null;
        try {
            indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int failed = indexResponse.getShardInfo().getFailed();
        return failed;
    }
}
