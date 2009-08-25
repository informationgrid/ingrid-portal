package de.ingrid.portal.jcaptcha;

import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class CaptchaServiceSingleton {

	private static ImageCaptchaService instance = initializeService();

	public static ImageCaptchaService getInstance() {
		return instance;
	}

	
	/**
	 * 
	 * @return Returns the ImageCaptchaService instance
	 */
	private static ImageCaptchaService initializeService() {

		// We need a instance of a own engine
		ListImageCaptchaEngine engine = new CustomCaptchaEngine();

		CaptchaStore captchaStore = new FastHashMapCaptchaStore();
		captchaStore.empty();

		ImageCaptchaService service = new DefaultManageableImageCaptchaService(
				captchaStore, engine, 180, 100000, 75000);

		return service;

	}

}
