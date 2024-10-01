package com.luxx.log.client;

import com.luxx.log.util.DateTimeUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

@Component
@Lazy
public class ElasticSearchClient {
    private static Logger logger = LogManager.getLogger(ElasticSearchClient.class);

    @Value("${es.address}")
    private String esAddress;

    @Value("${es.username}")
    private String username;

    @Value("${es.password}")
    private String password;

    @Value("${es.index}")
    private String indexName;

    // ES Client
    private RestHighLevelClient client;

    @PostConstruct
    public void init() throws UnknownHostException {
        logger.info("es.address: " + esAddress);
        logger.info("es.index: " + indexName);

        String[] hostPort = esAddress.split(":");
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostPort[0], Integer.parseInt(hostPort[1])))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY,
                                new UsernamePasswordCredentials(username, password));
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }));
    }

    @PreDestroy
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }

    public void indexLog(List<String> logList) {
        if (logList != null && logList.size() > 0) {
            BulkRequest request = new BulkRequest();
            for (String data : logList) {
                String month = DateTimeUtil.currentYM();
                String index = this.indexName + "_" + month;
                request.add(new IndexRequest(index).source(data, XContentType.JSON).type("_doc"));
            }
            BulkResponse bulkResponse;
            try {
                bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
                if (bulkResponse.hasFailures()) {
                    logger.error(bulkResponse.buildFailureMessage());
                }
            } catch (IOException e) {
                logger.error(e.toString());
            }
            logger.info("Index {} log to ES", logList.size());
        }
    }


}


# changes made by vinay in feature-2.2

{
  "short_name": "React App",
  "name": "Create React App Sample",
  "icons": [
    {
      "src": "favicon.ico",
      "sizes": "64x64 32x32 24x24 16x16",
      "type": "image/x-icon"
    },
    {
      "src": "logo192.png",
      "type": "image/png",
      "sizes": "192x192"
    },
    {
      "src": "logo512.png",
      "type": "image/png",
      "sizes": "512x512"
    }
  ],
  "start_url": ".",
  "display": "standalone",
  "theme_color": "#000000",
  "background_color": "#ffffff"
=======
## Changes made by developer Vikas in feature-2.1


module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:react/jsx-runtime',
    'plugin:react-hooks/recommended',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs'],
  parserOptions: { ecmaVersion: 'latest', sourceType: 'module' },
  settings: { react: { version: '18.2' } },
  plugins: ['react-refresh'],
  rules: {
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],
  },

}