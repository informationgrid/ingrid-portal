/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.dwr.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ingrid.mdek.Config;
import de.ingrid.mdek.SpringConfiguration;
import de.ingrid.mdek.util.MdekSecurityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LFSServiceImpl {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(LFSServiceImpl.class);

    private final String baseUrl;
    private final String movePath;
    private final String listPath;

    @Autowired
    public LFSServiceImpl(SpringConfiguration springConfig) {
        Config config = springConfig.globalConfig();
        this.baseUrl = config.bawRestApiBaseURL;
        this.movePath = config.bawRestApiMovePath;
        this.listPath = config.bawRestApiListPath;
    }

    public List<Map<String, Object>> getSubTree(String folder) throws Exception {

        HttpGet method = new HttpGet(baseUrl + listPath + folder);
        String result = this.request(method);

        return convertStringToList(result);
    }

    public Map<String, Object> initMove(String bwastrId, String psp, String file) throws IOException {
        String username = MdekSecurityUtils.getCurrentPortalUserData().getPortalLogin();
        HttpPut method = new HttpPut(baseUrl + movePath);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("bwastrId", bwastrId));
        params.add(new BasicNameValuePair("psp", psp));
        params.add(new BasicNameValuePair("user", username));
        params.add(new BasicNameValuePair("file", file));
        method.setEntity(new UrlEncodedFormEntity(params));
        String result = this.request(method);
        return convertStringToMap(result);
    }

    private String request(HttpUriRequest method) throws IOException {
        HttpClient client = getClient();
        int status;
        HttpResponse response;
        response = client.execute(method);
        status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        } else {
            log.error("Request to LFS REST-API failed: " + status);
            String body = EntityUtils.toString(response.getEntity());
            Map<String, Object> message = convertStringToMap(body);
            String msg = (String) message.get("msg");
            if (message.containsKey("description")) msg += " => " + message.get("description");
            throw new HttpResponseException(status, "Received status code: " + status + " with message: " + msg);
        }

    }

    private Map<String, Object> convertStringToMap(String data) throws HttpResponseException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new HttpResponseException(500, "Problem converting response entity to string");
        }
    }

    private List<Map<String, Object>> convertStringToList(String data) throws HttpResponseException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new HttpResponseException(500, "Problem converting response entity to string");
        }
    }

    private HttpClient getClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();

        /*CredentialsProvider provider = new BasicCredentialsProvider();

        // Create the authentication scope
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);

        // Create credential pair
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);

        // Inject the credentials
        provider.setCredentials(scope, credentials);

        // Set the default credentials provider
        builder.setDefaultCredentialsProvider(provider);*/

        // pickup JRE wide proxy configuration
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        builder.setRoutePlanner(routePlanner);

        return builder.build();
    }
}
