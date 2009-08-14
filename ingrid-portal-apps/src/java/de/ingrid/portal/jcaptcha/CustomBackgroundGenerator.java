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
