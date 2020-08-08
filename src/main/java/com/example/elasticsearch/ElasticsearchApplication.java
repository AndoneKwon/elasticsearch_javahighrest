package com.example.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.storedscripts.GetStoredScriptRequest;
import org.elasticsearch.action.admin.cluster.storedscripts.PutStoredScriptRequest;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.MultiSearchTemplateResponse;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


@SpringBootApplication
public class ElasticsearchApplication {

    public static boolean createIndex(String INDEX_NAME, String TYPE_NAME, RestHighLevelClient client) throws IOException{
        XContentBuilder indexBuilder = jsonBuilder()
                .startObject()
                .startObject(TYPE_NAME)
                .startObject("properties")
                .startObject("contents")
                .field("type", "text")
                .field("index_options", "docs")
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("index_options", "docs")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        request.mapping(TYPE_NAME, indexBuilder);

        String ALIAS_NAME = "moive_auto_alias";
        request.alias(new Alias(ALIAS_NAME));

        // 인덱스 생성
        AcknowledgedResponse createIndexResponse =
                client.indices().create(request, RequestOptions.DEFAULT);

        boolean acknowledged = createIndexResponse.isAcknowledged();

        log.println("create : "+acknowledged);
        return acknowledged;
    }

    public static boolean deleteIndex(String INDEX_NAME, String TYPE_NAME, RestHighLevelClient client) throws IOException{
        DeleteIndexRequest request1 = new DeleteIndexRequest(INDEX_NAME);

        AcknowledgedResponse deleteResponse = client.indices().delete(request1,RequestOptions.DEFAULT);

        boolean isSuccess = deleteResponse.isAcknowledged();

        log.println("delete : "+deleteResponse);

        return isSuccess;
    }

    public static boolean insertDoc(String INDEX_NAME, String TYPE_NAME,RestHighLevelClient client) throws IOException {
        IndexRequest request = new IndexRequest(INDEX_NAME,TYPE_NAME);

        request.source(jsonBuilder()
        .startObject()
        .field("contents","test movies")
        .field("title","test_titles")
        .endObject()
        );

        try{
            IndexResponse response = client.index(request,RequestOptions.DEFAULT);
            return true;
        }catch (ElasticsearchException e){
            return false;
        }
    }
/*
    public static boolean searchDoc(String INDEX_NAME, String TYPE_NAME,String FIELD_NAME,RestHighLevelClient client) throws IOException{
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);


    }
*/

    public static void getCosineSimilarity(String typeName, RestHighLevelClient client) throws IOException{
/*
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.);
        SearchRequest request = new SearchRequest("x_test");
        request.source(searchSourceBuilder);
        System.out.println(request.toString());

        SearchResponse searchResponse = client.search(request,RequestOptions.DEFAULT);
        System.out.println(request.toString());
        System.out.println(searchResponse.getHits());

 */

        CreateIndexRequest request = new CreateIndexRequest("x_test");
        XContentBuilder builder = jsonBuilder().startObject().endObject();
        request.mapping("",builder);
        SearchTemplateRequest request1 = new SearchTemplateRequest();
        //request.setRequest(new SearchRequest("posts"));
        request1.setScriptType(ScriptType.INLINE);
        request1.setScript(request.mappings().toString());

        SearchTemplateResponse response = client.searchTemplate(request1, RequestOptions.DEFAULT);

        //System.out.println(response.toString());
//        request.setScriptParams(scriptParams);
    }

    public static void main(String[] args) throws IOException {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("117.17.196.61", 9200, "http")));

        String INDEX_NAME="x_test";

        //String TYPE_NAME = "my_type";

        //createIndex(INDEX_NAME,TYPE_NAME,client);

        //deleteIndex(INDEX_NAME,TYPE_NAME,client);

        //insertDoc(INDEX_NAME,TYPE_NAME,client);

        getCosineSimilarity(INDEX_NAME,client);

        //SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
