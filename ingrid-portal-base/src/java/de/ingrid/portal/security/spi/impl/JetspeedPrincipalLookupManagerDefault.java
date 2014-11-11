/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingrid.portal.security.spi.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.ingrid.portal.security.JetspeedPrincipalQueryContext;

/**
 * Default implementation of the principal lookup manager. This implementation
 * uses no database specifics.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public class JetspeedPrincipalLookupManagerDefault extends JetspeedPrincipalLookupManagerAbstract {

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.JetspeedPrincipalLookupManagerAbstract#getPagingSql(java.lang.String, org.apache.jetspeed.security.JetspeedPrincipalQueryContext)
	 */
	@Override
	protected String getPagingSql(String sql, JetspeedPrincipalQueryContext queryContext) {
		// no database specifics in the default manager
		return sql;
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.JetspeedPrincipalLookupManagerAbstract#scrollToOffset(java.sql.Connection, java.sql.ResultSet, long)
	 */
	@Override
	protected void scrollToOffset(Connection con, ResultSet rs, long offset) throws SQLException {
		// no database specifics in the default manager
		// must use JDBC based scrolling
		boolean supportsScrollable = false;
		if (con.getMetaData().supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {
			supportsScrollable = true;
		}
		if (supportsScrollable) {
			if (!rs.absolute((int) (offset + 1))) {
				supportsScrollable = false;
			}
		}
		if (!supportsScrollable) {
			for (int i = 0; i <= (offset - 1); i++) {
				if (rs.isAfterLast() || !rs.next()) {
					break;
				}
			}
		}
	}

}
