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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;

public class CustomBackgroundGenerator extends UniColorBackgroundGenerator {

	private BufferedImage backround;

	private ColorGenerator colorGenerator = null;

	public CustomBackgroundGenerator(Integer width, Integer height) {
		              this(width, height, Color.white);
    }


	public CustomBackgroundGenerator(Integer width, Integer height,
			Color color) {
		super(width, height);
		this.colorGenerator = new SingleColorGenerator(color);
	}

	
	public CustomBackgroundGenerator(Integer width, Integer height,
			ColorGenerator colorGenerator) {
		super(width, height);
		this.colorGenerator = colorGenerator;
	}


	/* (non-Javadoc)
	 * @see com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator#getBackground()
	 */
	public BufferedImage getBackground() {
		backround = new BufferedImage(getImageWidth(), getImageHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D pie = (Graphics2D) backround.getGraphics();
		Color color = colorGenerator.getNextColor();

		pie.setColor(color != null ? color : Color.white);
		pie.setBackground(color != null ? color : Color.white);
		pie.fillRect(0, 0, getImageWidth(), getImageHeight());
		pie.dispose();
		return backround;
	}

}
