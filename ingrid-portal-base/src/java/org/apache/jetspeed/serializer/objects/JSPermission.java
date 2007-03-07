/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.serializer.objects;

import java.util.ArrayList;
import java.util.Iterator;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.FragmentPermission;
import org.apache.jetspeed.security.PagePermission;
import org.apache.jetspeed.security.PortalResourcePermission;
import org.apache.jetspeed.security.PortletPermission;

/**
 * Serialized Permission <permission type='folder' resource='/' actions='view,
 * edit'> <roles>admin, user</roles> <groups>dev</groups> <users>joe</users>
 * </permission>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSPermission
{

	private String type;

	private String resource;

	private String actions;

	private long id;

	private ArrayList roles = null;

	private ArrayList groups = null;

	private ArrayList users = null;

	private JSUserRoles roleString;

	private JSUserGroups groupString;

	private JSUserUsers userString;

	public static String TYPE_FOLDER = "folder".intern();

	public static String TYPE_FRAGMENT = "fragment".intern();

	public static String TYPE_PAGE = "page".intern();

	public static String TYPE_PORTALRESOURCE = "portalResource".intern();

	public static String TYPE_PORTALRESOURCECOLLECTION = "portalResource"
			.intern();

	public static String TYPE_PORTAL = "portal".intern();

	public static String TYPE_UNKNOWN = "unknown".intern();

	public String getClassForType(String type)
	{
		if ((type == null) || (type.length() == 0) || (type == TYPE_UNKNOWN))
			return "";
		if (type == TYPE_FOLDER)
			return "org.apache.jetspeed.security.FolderPermission";
		if (type == TYPE_FRAGMENT)
			return "org.apache.jetspeed.security.FragmentPermission";
		if (type == TYPE_PAGE)
			return "org.apache.jetspeed.security.PagePermission";
		if (type == TYPE_PORTALRESOURCE)
			return "org.apache.jetspeed.security.PortalResourcePermission";
		if (type == TYPE_PORTALRESOURCECOLLECTION)
			return "org.apache.jetspeed.security.PortalResourcePermissionCollection";
		if (type == TYPE_PORTAL)
			return "org.apache.jetspeed.security.PortletPermission";
		return "";
	}

	public String getTypeForClass(String className)
	{
		if ((className == null) || (className.length() == 0))
			return TYPE_UNKNOWN;
		if (className.equals("org.apache.jetspeed.security.FolderPermission"))
			return TYPE_FOLDER;

		if (className.equals("org.apache.jetspeed.security.FragmentPermission"))
			return TYPE_FRAGMENT;
		if (className.equals("org.apache.jetspeed.security.PagePermission"))
			return TYPE_PAGE;
		if (className.equals("org.apache.jetspeed.security.PortletPermission"))
			return TYPE_PORTAL;

		if (className
				.equals("org.apache.jetspeed.security.PortalResourcePermission"))
			return TYPE_PORTALRESOURCE;
		if (className
				.equals("org.apache.jetspeed.security.PortalResourcePermissionCollection"))
			return TYPE_PORTALRESOURCECOLLECTION;
		return TYPE_UNKNOWN;

	}

	public PortalResourcePermission getPermissionForType()
	{
		PortalResourcePermission newPermission = null; 
		if ((this.type == null) || (this.type == TYPE_UNKNOWN))
			return null;
		try
		{
		if (type.equals(TYPE_FOLDER))
			newPermission = new FolderPermission(this.resource,this.actions);
		else if (type.equals(TYPE_FRAGMENT))
			newPermission = new FragmentPermission(this.resource,this.actions);
			else if (type.equals(TYPE_PAGE))
				newPermission = new PagePermission(this.resource,this.actions);
				else if (type.equals(TYPE_PORTAL))
					newPermission = new PortletPermission(this.resource,this.actions);
					else return null;
			return newPermission;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	public JSPermission()
	{
	}

	private String append(JSRole rule)
	{
		return rule.getName();
	}

	private String append(JSGroup group)
	{
		return group.getName();
	}

	private String append(JSUser user)
	{
		return user.getName();
	}

	private String append(Object s)
	{
		if (s instanceof JSRole)
			return append((JSRole) s);
		if (s instanceof JSGroup)
			return append((JSGroup) s);
		if (s instanceof JSUser)
			return append((JSUser) s);

		return s.toString();
	}

	private String putTokens(ArrayList _list)
	{
		if ((_list == null) || (_list.size() == 0))
			return "";
		boolean _start = true;
		Iterator _it = _list.iterator();
		StringBuffer _sb = new StringBuffer();
		while (_it.hasNext())
		{
			if (!_start)
				_sb.append(',');
			else
				_start = false;

			_sb.append(append(_it.next()));
		}
		return _sb.toString();
	}

	/**
	 * @return Returns the actions.
	 */
	public String getActions()
	{
		return actions;
	}

	/**
	 * @param actions
	 *            The actions to set.
	 */
	public void setActions(String actions)
	{
		this.actions = actions;
	}

	/**
	 * @return Returns the groups.
	 */
	public ArrayList getGroups()
	{
		return groups;
	}

	/**
	 * @param groups
	 *            The groups to set.
	 */
	public void setGroups(ArrayList groups)
	{
		this.groups = groups;
	}

	/**
	 * @return Returns the resource.
	 */
	public String getResource()
	{
		return resource;
	}

	/**
	 * @param resource
	 *            The resource to set.
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}

	/**
	 * @return Returns the roles.
	 */
	public ArrayList getRoles()
	{
		return roles;
	}

	/**
	 * @param roles
	 *            The roles to set.
	 */
	public void setRoles(ArrayList roles)
	{
		this.roles = roles;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return Returns the users.
	 */
	public ArrayList getUsers()
	{
		return users;
	}

	/**
	 * @param users
	 *            The users to set.
	 */
	public void setUsers(ArrayList users)
	{
		this.users = users;
	}


	/**
	 * @return Returns the id.
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	

	public void addGroup(JSGroup group)
	{
		if (groups == null)
			groups = new ArrayList();
		groups.add(group);
	}

	public void addRole(JSRole role)
	{
		if (roles == null)
			roles = new ArrayList();
		roles.add(role);
	}


	public void addUser(JSUser user)
	{
		if (users == null)
			users = new ArrayList();
		users.add(user);
	}


	/***************************************************************************
	 * SERIALIZER
	 */
	private static final XMLFormat XML = new XMLFormat(JSPermission.class)
	{
		public void write(Object o, OutputElement xml)
				throws XMLStreamException
		{
			try
			{
				JSPermission g = (JSPermission) o;
				xml.setAttribute("type", g.getType());
				xml.setAttribute("resource",g.getResource());
				xml.setAttribute("actions",g.getActions());
				g.groupString = new JSUserGroups(g.putTokens(g.getGroups()));
				g.roleString = new JSUserRoles(g.putTokens(g.getRoles())); 
				g.userString = new JSUserUsers(g.putTokens(g.getUsers())); 
				xml.add(g.roleString);
				xml.add(g.groupString);
				xml.add(g.userString);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void read(InputElement xml, Object o)
		{
			try
			{
				JSPermission g = (JSPermission) o;
				g.type = StringEscapeUtils.unescapeHtml(xml.getAttribute("type", "type_unknown"));
				g.resource = StringEscapeUtils.unescapeHtml(xml.getAttribute("resource", "resource_unknown"));
				g.actions = StringEscapeUtils.unescapeHtml(xml.getAttribute("actions", "unknown_actions"));
				
	               while (xml.hasNext())
	                {
	                    Object o1 = xml.getNext(); // mime

	                    if (o1 instanceof JSUserGroups)
	                        g.groupString = (JSUserGroups) o1;
	                    else if (o1 instanceof JSUserUsers)
	                        g.userString = (JSUserUsers) o1;
	                    else if (o1 instanceof JSUserRoles)
	                        g.roleString = (JSUserRoles) o1;
	                }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	};

	public JSUserGroups getGroupString()
	{
		return groupString;
	}

	public JSUserRoles getRoleString()
	{
		return roleString;
	}

	public JSUserUsers getUserString()
	{
		return userString;
	}


	
}