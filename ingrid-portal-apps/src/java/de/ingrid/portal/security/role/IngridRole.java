/*
 * **************************************************-
 * ingrid-portal-utils
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.security.role;

/**
 * Class for ingrid specific roles. This Class holds ingrid role constants 
 * at the moment, but should be extended if ingrid specific role functionality
 * must be implemented.
 *
 * @author joachim@wemove.com
 */
public class IngridRole {

    public static final String ROLE_ADMIN_PORTAL = "admin-portal";
    public static final String ROLE_ADMIN_PARTNER = "admin-partner";
    public static final String ROLE_ADMIN_PROVIDER = "admin-provider";

}
