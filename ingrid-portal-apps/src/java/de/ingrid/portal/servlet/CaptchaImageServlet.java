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

import com.octo.captcha.service.CaptchaServiceException;

import de.ingrid.portal.jcaptcha.CaptchaServiceSingleton;

public class CaptchaImageServlet extends HttpServlet{
	
	private static final long serialVersionUID = -9167034970446594129L;


	public void init(ServletConfig servletConfig) throws ServletException {

        super.init(servletConfig);

    }


    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

       byte[] captchaChallengeAsPng = null;
       // the output stream to render the captcha image as jpeg into
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
        // get the session id that will identify the generated captcha.
        //the same id must be used to validate the response, the session id is a good candidate!
        String captchaId = httpServletRequest.getSession().getId();
        // call the ImageCaptchaService getChallenge method
            BufferedImage challenge =
                    CaptchaServiceSingleton.getInstance().getImageChallengeForID(captchaId,
                            httpServletRequest.getLocale());

            // a jpeg encoder
            /*   JPEGImageEncoder jpegEncoder =
                    JPEGCodec.createJPEGEncoder(jpegOutputStream);
            jpegEncoder.encode(challenge);
        	
            */
            // a png
            
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
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsPng);
        responseOutputStream.flush();
        responseOutputStream.close();
    }	
}
