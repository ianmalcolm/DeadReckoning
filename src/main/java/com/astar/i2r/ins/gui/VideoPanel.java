package com.astar.i2r.ins.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

class VideoPanel extends JPanel {
	private BufferedImage image = null;

	public void updateImage(Mat matrix) {

		matrix = resize(matrix);

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		byte[] b = new byte[matrix.channels() * matrix.cols() * matrix.rows()];
		matrix.get(0, 0, b);
		image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);

		repaint();
	}

//	private BufferedImage mat2BufferedImage(Mat mat) {
//		int type = 0;
//		if (mat.channels() == 1) {
//			type = BufferedImage.TYPE_BYTE_GRAY;
//		} else if (mat.channels() == 3) {
//			type = BufferedImage.TYPE_3BYTE_BGR;
//		} else {
//			return null;
//		}
//
//		BufferedImage bImage = new BufferedImage(mat.width(), mat.height(),
//				type);
//		WritableRaster raster = image.getRaster();
//		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
//		byte[] data = dataBuffer.getData();
//		mat.get(0, 0, data);
//
//		return bImage;
//	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image == null) {
			return;
		}
		g.drawImage(this.image, 1, 1, this.image.getWidth(),
				this.image.getHeight(), null);
	}

	private Mat resize(Mat matrix) {
		Size newSize = null;

		Size origSize = matrix.size();
		double origRatio = origSize.width / origSize.height;
		Dimension panelSize = this.getSize();
		double panelRatio = panelSize.width / panelSize.height;
		if (origRatio > panelRatio) {
			double ratio = panelSize.width / origSize.width;
			newSize = new Size(panelSize.width, origSize.height * ratio);
		} else {
			double ratio = panelSize.height / origSize.height;
			newSize = new Size(origSize.width * ratio, panelSize.height);
		}
		Mat outMatrix = new Mat();
		Imgproc.resize(matrix, outMatrix, newSize);
		return outMatrix;
	}
}
