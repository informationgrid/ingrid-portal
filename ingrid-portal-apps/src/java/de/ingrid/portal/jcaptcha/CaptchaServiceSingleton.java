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

	private static ImageCaptchaService initializeService() {

		// Wir brauchen eine Instanz unser eigenen Engine
		ListImageCaptchaEngine engine = new CustomCaptchaEngine();

		CaptchaStore captchaStore = new FastHashMapCaptchaStore();
		captchaStore.empty();

		ImageCaptchaService service = new DefaultManageableImageCaptchaService(
				captchaStore, engine, 180, 100000, 75000);

		return service;

	}

}
