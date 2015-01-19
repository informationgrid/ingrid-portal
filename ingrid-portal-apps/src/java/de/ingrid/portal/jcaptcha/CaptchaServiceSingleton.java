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
