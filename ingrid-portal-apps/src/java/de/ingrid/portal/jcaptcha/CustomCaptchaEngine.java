/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.awt.Color;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.fontgenerator.DeformedRandomFontGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptchaFactory;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class CustomCaptchaEngine extends ListImageCaptchaEngine {

	protected void buildInitialFactories() {

		// RGB: #4C4E42
		Color textColor = new Color(0x4c, 0x4e, 0x42);
		Color textColor2 = Color.BLACK;
		Color[] colors = new Color[2];
		colors[0] = textColor;
		colors[1] = textColor2;

		// background color ARGB (red, green, blue, alpha)
		Color backgroundColor = new Color(0xff, 0xff, 0xff, 0);

		// CustomBackgroundGenerator with ARGB
		BackgroundGenerator bgGenerator = new CustomBackgroundGenerator(210,
				70, backgroundColor);

		// font (min word length, max word length, font color)
		TextPaster paster = new RandomTextPaster(6, 6, colors);

		// font size (min 20, max 35)
		FontGenerator fontGenerator = new DeformedRandomFontGenerator(20, 35);

		// add font du image
		WordToImage wordToImage = new ComposedWordToImage(fontGenerator,
				bgGenerator, paster);

		// random word
		WordGenerator wordGenerator = new RandomWordGenerator(
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ");

		ImageCaptchaFactory factory = new GimpyFactory(wordGenerator,
				wordToImage);

		this.addFactory(factory);
	}

}
