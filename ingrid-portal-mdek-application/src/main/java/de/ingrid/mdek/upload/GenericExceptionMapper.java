/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.upload;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(Throwable ex) {
        // use response status in case of WebApplicationException
        int status = ex instanceof WebApplicationException ?
                ((WebApplicationException)ex).getResponse().getStatus() :
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        // create the upload response (add entity only for JSON)
        UploadResponse response = null;
        List<MediaType> accepts = this.headers.getAcceptableMediaTypes();
        if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
            response = new UploadResponse(ex);
        }
        return Response.status(status).entity(response).build();
    }
}
