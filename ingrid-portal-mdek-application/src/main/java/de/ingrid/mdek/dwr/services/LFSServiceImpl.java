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
import de.ingrid.mdek.beans.TreeNodeBean;
import de.ingrid.mdek.handler.AddressRequestHandler;
import de.ingrid.mdek.handler.ObjectRequestHandler;
import de.ingrid.mdek.i18n.MdekResourceBundle;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.net.ProxySelector;
import java.util.*;

public class LFSServiceImpl {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(LFSServiceImpl.class);


    public List<Map<String, Object>> getSubTree(String folder) {

        List<Map<String, Object>> data = null;

        HttpClient client = getClient();
        HttpGet method = new HttpGet("http://localhost:3000/api/list?folder=" + folder);
        String result;
        int status;
        HttpResponse response;
        try {
            response = client.execute(method);
            status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                result = EntityUtils.toString( response.getEntity() );
                ObjectMapper mapper = new ObjectMapper();
                data = mapper.readValue(result, new TypeReference<List<Map<String, Object>>>(){});
            } else {
                log.error("Could not connect to LFS REST-API. Status: " + status);
                return null;
            }

        } catch (Exception ex) {
            log.error("Cannot get LFS Subtree", ex);
        }

        return data;
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
