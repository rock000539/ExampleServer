/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.business.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedString;
import java.util.Date;
import javax.swing.ImageIcon;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 報表浮水印生成工具
 *
 * @author Parker Huang
 * @since 1.0.0
 */
public class WaterMarkUtil {

	public static BufferedImage getWebWatermarkBufferredImage(String accountNo, String userName, String branCode, Date specifyDate) throws Exception {
		BufferedImage singleWaterImage = genWebWatermarkBufferedImage(accountNo, userName, branCode, specifyDate);
		// 進行背景透明化
		return transferAlpha(singleWaterImage);
	}

	/**
	 * 產出網頁單一格浮水印
	 *
	 * @param accountNo 帳號
	 * @param userName 使用者名稱
	 * @param branCode 分行代碼
	 * @param specifyDate 指定顯示時間
	 * @return
	 */
	public static BufferedImage genWebWatermarkBufferedImage(String accountNo, String userName, String branCode, Date specifyDate) throws Exception {
		int width = 180, height = 150;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = bi.getGraphics();

		Graphics2D g2d = (Graphics2D) g;

		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, height);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));

		Font font1 = new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 24);
		Font font2 = new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 18);
		AffineTransform at = new AffineTransform();
		// 旋轉圖片
		double p = Math.PI / 6.0;
		at.setToRotation(-p);
		g2d.setTransform(at);

		AttributedString as1 = new AttributedString(accountNo + " " + branCode);
		if (specifyDate == null) {
			specifyDate = new Date();
		}
		AttributedString as2 = new AttributedString("CRM3 " + DateFormatUtils.format(specifyDate, "yyyy/MM/dd"));
		AttributedString as3 = new AttributedString(DateFormatUtils.format(specifyDate, "HH:mm"));

		as1.addAttribute(TextAttribute.FONT, font1);
		as2.addAttribute(TextAttribute.FONT, font2);
		as3.addAttribute(TextAttribute.FONT, font2);
		g2d.setColor(new Color(100, 100, 100));
		g2d.drawString(as1.getIterator(), -7, 100);
		g2d.drawString(as2.getIterator(), -7, 125);
		g2d.drawString(as3.getIterator(), -7, 150);
		g.dispose();
		return bi;
	}

	public static BufferedImage transferAlpha(java.awt.Image image) throws IOException {
		ImageIcon imageIcon = new ImageIcon(image);
		BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
		g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
		int alpha = 0;
		for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage.getHeight(); j1++) {
			for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage.getWidth(); j2++) {
				int rgb = bufferedImage.getRGB(j2, j1);

				int R = (rgb & 0xff0000) >> 16;
				int G = (rgb & 0xff00) >> 8;
				int B = (rgb & 0xff);
				if (((255 - R) < 30) && ((255 - G) < 30) && ((255 - B) < 30)) {
					rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
				}
				bufferedImage.setRGB(j2, j1, rgb);
			}
		}
		g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
		return bufferedImage;
	}
}
