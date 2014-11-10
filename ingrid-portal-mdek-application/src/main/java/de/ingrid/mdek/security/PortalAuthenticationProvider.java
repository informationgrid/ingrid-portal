package de.ingrid.mdek.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class PortalAuthenticationProvider implements AuthenticationProvider {
    
    private final static Logger log = Logger.getLogger(PortalAuthenticationProvider.class);  

    /**
     * Since authentication id done via the portal we can return always false here, since 
     * user should be written in session already.
     */
    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public List<String> getAllUserIds() {
        List<String> userList = new ArrayList<String>();
        
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();

        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");

        try {
            UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
            RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
            List<String> users = userManager.getUserNames("");

            for (String user : users) {
                if (!user.equals("admin") && !user.equals("guest") && !user.equals("devmgr")
                     && !roleManager.isUserInRole(user, "admin")
                     && !roleManager.isUserInRole(user, "admin-portal")
                     && !roleManager.isUserInRole(user, "mdek")) {
                    userList.add(user);
                }
            }

        } catch (Exception err) {
            log.error("Exception: ", err);
            throw new RuntimeException("Error while fetching users from the portal context.", err);
        }

        Collections.sort(userList);
        return userList;
    }

    @Override
    public String getForcedUser(HttpServletRequest req) {
        // if we come from an DWR call, we need to get the request
        HttpSession ses = null;
        if (req == null) {
            WebContext wctx = WebContextFactory.get();
            req = wctx.getHttpServletRequest();
            ses = wctx.getSession();
        } else {
            ses = req.getSession();
        }
        
        ServletContext ctx = ses.getServletContext().getContext("/ingrid-portal-mdek");

        String forcedIgeUser = null;
        if (ctx != null) {
            forcedIgeUser = (String) ctx.getAttribute("ige.force.userName");
        }
        return forcedIgeUser;
    }

    @Override
    public void setIgeUser(String username) {
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();
        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
        RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        try {
            roleManager.addRoleToUser(username, "mdek");
        } catch (SecurityException e) {
            log.debug("SecurityException while setting role 'mdek' to user: " + username, e);
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void removeIgeUser(String username) {
        WebContext wctx = WebContextFactory.get();
        HttpSession session = wctx.getSession();
        ServletContext ctx = session.getServletContext().getContext("/ingrid-portal-mdek");
        UserManager userManager = (UserManager) ctx.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (userManager.userExists(username)) {
            RoleManager roleManager = (RoleManager) ctx.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
            try {
                roleManager.removeRoleFromUser(username, "mdek");
            } catch (SecurityException e) {
                log.debug("SecurityException while removing role 'mdek' from user: " + username, e);
                throw new RuntimeException(e);
            }
        }
    }

}
