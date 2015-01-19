/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.octo.captcha.service.CaptchaServiceException;

import de.ingrid.portal.jcaptcha.CaptchaServiceSingleton;

public class CaptchaImageServlet extends HttpServlet {

	private static final long serialVersionUID = -9167034970446594129L;
	private Cache captchaCache = null;

	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);
		captchaCache = CacheManager.create().getCache("de.ingrid.portal.servlet.jcaptcha");
	}

	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		byte[] captchaChallengeAsPng = null;
		BufferedImage challenge = null;

		String sessionId = httpServletRequest.getSession().getId();

		if (httpServletRequest.getParameter("keep") != null) {
			if (captchaCache.isKeyInCache(sessionId)) {
				challenge = (BufferedImage) captchaCache.get(sessionId).getObjectValue();
			}
		}

		if (challenge == null) {
			// call the ImageCaptchaService getChallenge method
			challenge = CaptchaServiceSingleton.getInstance().getImageChallengeForID(sessionId, httpServletRequest.getLocale());
		}
		
		Element cacheElement = new Element(sessionId, challenge);
		captchaCache.put(cacheElement);

		// the output stream to render the captcha image as jpeg into
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(challenge, "png", pngOutputStream);
		} catch (IllegalArgumentException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		} catch (CaptchaServiceException e) {
			httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		captchaChallengeAsPng = pngOutputStream.toByteArray();

		// flush it in the response
		httpServletResponse.setHeader("Cache-Control", "no-store");
		httpServletResponse.setHeader("Pragma", "no-cache");
		httpServletResponse.setDateHeader("Expires", 0);
		httpServletResponse.setContentType("image/png");
		ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
		responseOutputStream.write(captchaChallengeAsPng);
		responseOutputStream.flush();
		responseOutputStream.close();
	}
}
