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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.accesslayer.RowReader;
import org.apache.ojb.broker.query.QueryByCriteria;

import de.ingrid.portal.security.JetspeedPrincipalQueryContext;
import de.ingrid.portal.security.JetspeedPrincipalResultList;
import de.ingrid.portal.security.spi.JetspeedPrincipalLookupManager;

/**
 * Abstract base class for the principal lookup manager. Defines possible
 * database specific abstract methods and provides more generic methods to all
 * database specific principal lookup managers.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public abstract class JetspeedPrincipalLookupManagerAbstract implements JetspeedPrincipalLookupManager {

	static final Log log = LogFactory.getLog(JetspeedPrincipalLookupManagerAbstract.class);
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.jetspeed.security.spi.JetspeedPrincipalPersistanceManager#
	 * getPrincipals(org.apache.jetspeed.security.JetspeedPrincipalQueryContext)
	 */
	public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext) {
		String baseSqlStr = this.generateBaseSql(queryContext);

		// create paging sql statement if possible for database based paging
		String sqlStr = getPagingSql(baseSqlStr, queryContext);

		int numberOfRecords = 0;
		ArrayList<InternalUserPrincipal> results = new ArrayList<InternalUserPrincipal>();
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = PersistenceBrokerFactory.defaultPersistenceBroker().serviceConnectionManager().getConnection();

			pstmt = conn.prepareStatement(sqlStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// pstmt = conn.prepareStatement(sqlStr,
			// ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize((int) (queryContext.getOffset() + queryContext.getLength()));
			ResultSet rs = pstmt.executeQuery();
			boolean hasRecords = rs.next();

			if (hasRecords) {
				// scroll the result set to the offset
				scrollToOffset(conn, rs, queryContext.getOffset());
				for (int i = 0; i < queryContext.getLength(); i++) {
					// now materialize the ResultSet into a JetspeedPrincipal
					RowReader rr = PersistenceBrokerFactory.defaultPersistenceBroker().getClassDescriptor(
					        InternalUserPrincipalImpl.class).getRowReader();
					Map<Object, Object> row = new HashMap<Object, Object>();
					// TODO: optimize, just retrieve the id from the DB and setup
					// a JetspeedPrincipal template on that.
					rr.readObjectArrayFrom(rs, row);
					InternalUserPrincipalImpl p = (InternalUserPrincipalImpl) rr.readObjectFrom(row);
					QueryByCriteria query = new QueryByCriteria(p);
					p = (InternalUserPrincipalImpl) PersistenceBrokerFactory.defaultPersistenceBroker()
							.getObjectByQuery(query);
					results.add(p);
					if (!rs.next()) {
						break;
					}
				}
				rs.close();

				// get the total number of results effected by the query
				int fromPos = baseSqlStr.toUpperCase().indexOf(" FROM ");
				if (fromPos >= 0) {
					baseSqlStr = "select count(security_principal.principal_id)" + baseSqlStr.substring(fromPos);
				}
				// strip ORDER BY clause
				int orderPos = baseSqlStr.toUpperCase().indexOf(" ORDER BY ");
				if (orderPos >= 0) {
					baseSqlStr = baseSqlStr.substring(0, orderPos);
				}
				pstmt = conn.prepareStatement(baseSqlStr);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					numberOfRecords += rs.getInt(1);
				}
				rs.close();
			}
		} catch (SQLException e) {
			log.error("Error reading principal.", e);
		} catch (PBFactoryException e) {
			log.error("Error reading principal.", e);
		} catch (LookupException e) {
			log.error("Error reading principal.", e);
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error("Error closing connection.", e);
			}
		}
		return new JetspeedPrincipalResultList(results, numberOfRecords);
	}

	/**
	 * Generate the base SQL syntax for selecting principals. This must not
	 * contain any database specifics.
	 * 
	 * @param queryContext
	 * @return
	 */
	protected String generateBaseSql(JetspeedPrincipalQueryContext queryContext) {
		String attributeConstraint = null;
		String fromPart = "security_principal";
		if (queryContext.getSecurityAttributes() != null) {
			int cnt = 1;
			for (Map.Entry<String, String> attribute : queryContext.getSecurityAttributes().entrySet()) {
				if (attributeConstraint == null) {
					attributeConstraint = "pnb" + cnt + ".FULL_PATH=security_principal.FULLPATH AND pnb" + cnt + ".NODE_ID=pn" + cnt 
					+ ".PARENT_NODE_ID AND pn" + cnt + ".NODEID=pv" + cnt + ".NODE_ID AND pv" + cnt + ".PROPERTY_NAME='" + attribute.getKey() + "' AND pv" + cnt + ".PROPERTY_VALUE LIKE '" + attribute.getValue() + "'"; 
				} else {
                    attributeConstraint = " AND pnb" + cnt + ".FULL_PATH=security_principal.FULLPATH AND pnb" + cnt + ".NODE_ID=pn" + cnt 
                    + ".PARENT_NODE_ID AND pn" + cnt + ".NODEID=pv" + cnt + ".NODE_ID AND pv" + cnt + ".PROPERTY_NAME='" + attribute.getKey() + "' AND pv" + cnt + ".PROPERTY_VALUE LIKE '" + attribute.getValue() + "'"; 
				}
				fromPart += ", prefs_node pnb" + cnt + ", prefs_node pn" + cnt + ", prefs_property_value pv" + cnt;
				cnt++;
			}
		}

		String constraint = null;
		if (queryContext.getNameFilter() != null && queryContext.getNameFilter().length() > 0) {
			constraint = "pnb.FULL_PATH=security_principal.FULLPATH AND pnb.NODE_NAME LIKE '"+queryContext.getNameFilter().replace('*', '%')+"'";
			fromPart += ", prefs_node pnb";
		}

		// find principals that are member of one or many roles
		// the principal must be member in all supplied roles.
		String roleConstraints = null;
		if (queryContext.getAssociatedRoles() != null && queryContext.getAssociatedRoles().size() > 0
				&& queryContext.getAssociatedRoles().get(0).length() > 0) {
			int cnt = 1;
			for (String roleName : queryContext.getAssociatedRoles()) {
				if (roleConstraints == null) {
				    roleConstraints = "sur" + cnt + ".USER_ID=security_principal.PRINCIPAL_ID AND sur" + cnt 
				        + ".ROLE_ID=spr.PRINCIPAL_ID AND pnr" + cnt + ".FULL_PATH=sur" + cnt 
				        + ".FULLPATH AND pnr" + cnt + ".NODE_NAME LIKE '" + roleName + "'";
				} else {
                    roleConstraints = " AND sur" + cnt + ".USER_ID=security_principal.PRINCIPAL_ID AND sur" + cnt 
                    + ".ROLE_ID=spr.PRINCIPAL_ID AND pnr" + cnt + ".FULL_PATH=sur" + cnt 
                    + ".FULLPATH AND pnr" + cnt + ".NODE_NAME LIKE '" + roleName + "'";
				}
			}
			fromPart += ", security_user_role sur" + cnt + ", security_principal spr" + cnt + ", prefs_node pnr" + cnt;
			cnt++;
		}

		// find principals that are member of one or many groups
		// the principal must be member in all supplied groups.
		String groupConstraints = null;
		if (queryContext.getAssociatedGroups() != null && queryContext.getAssociatedGroups().size() > 0
				&& queryContext.getAssociatedGroups().get(0).length() > 0) {
			int cnt = 1;
			for (String groupName : queryContext.getAssociatedGroups()) {
				if (groupConstraints == null) {
                    groupConstraints = "sug" + cnt + ".USER_ID=security_principal.PRINCIPAL_ID AND sug" + cnt 
                    + ".GROUP_ID=spg" + cnt + ".PRINCIPAL_ID AND png" + cnt + ".FULL_PATH=sug" + cnt 
                    + ".FULLPATH AND png" + cnt + ".NODE_NAME LIKE '" + groupName + "'";
				} else {
                    groupConstraints = " AND sug" + cnt + ".USER_ID=security_principal.PRINCIPAL_ID AND sug" + cnt 
                    + ".GROUP_ID=spg" + cnt + ".PRINCIPAL_ID AND png" + cnt + ".FULL_PATH=sug" + cnt 
                    + ".FULLPATH AND png" + cnt + ".NODE_NAME LIKE '" + groupName + "'";
				}
			}
            fromPart += ", security_user_group sug" + cnt + ", security_principal spg" + cnt + ", prefs_node png" + cnt;
			cnt++;
		}

		if (attributeConstraint != null) {
			if (constraint != null) {
				constraint += " AND " + attributeConstraint;
			} else {
				constraint = attributeConstraint;
			}
		}

		if (roleConstraints != null) {
			if (constraint != null) {
				constraint += " AND " + roleConstraints;
			} else {
				constraint = roleConstraints;
			}
		}

		if (groupConstraints != null) {
			if (constraint != null) {
				constraint += " AND " + groupConstraints;
			} else {
				constraint = groupConstraints;
			}
		}

		String baseSqlStr = "SELECT security_principal.* from " + fromPart + " WHERE IS_ENABLED=1";
		
		if (constraint != null) {
			baseSqlStr += " AND " + constraint;
		}
		
		if (queryContext.getOrder() != null && queryContext.getOrder().equalsIgnoreCase("desc")) {
			baseSqlStr += " ORDER BY security_principal.FULL_PATH DESC";
		} else {
			baseSqlStr += " ORDER BY security_principal.FULL_PATH";
		}
		return baseSqlStr;
	}

	/**
	 * Add database specific paging to the SQL.
	 * 
	 * @param sql
	 * @param queryContext
	 * @return
	 */
	protected abstract String getPagingSql(String sql, JetspeedPrincipalQueryContext queryContext);

	/**
	 * Add database specific code for scrolling the dataset to the specified
	 * offset.
	 * 
	 * @param con
	 * @param rs
	 * @param offset
	 * @throws SQLException
	 */
	protected abstract void scrollToOffset(Connection con, ResultSet rs, long offset) throws SQLException;

}
