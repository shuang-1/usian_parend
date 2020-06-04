package com.usian.service;


import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import com.usian.utils.JsonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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
                            (JsonUtils.objectToJson(searchItem), XContentType.JSON));
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
                "}",XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest,RequestOptions.DEFAULT);
    }
}
