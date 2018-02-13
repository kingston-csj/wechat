package com.kingston.chat.util;

import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public final class ImageUtil {

	/**
	 * 转化为灰度图
	 * @throws IOException
	 */
	public static Image convertToGray(Image image) {
		PixelReader pixelReader = image.getPixelReader();
		WritableImage grayImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
		PixelWriter pixelWriter = grayImage.getPixelWriter();

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color color = pixelReader.getColor(x, y);
				color = color.grayscale();
				pixelWriter.setColor(x, y, color);
			}
		}
		return grayImage;
	}

}
