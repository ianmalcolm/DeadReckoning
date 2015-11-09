package com.astar.i2r.ins.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolTip;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import com.astar.i2r.ins.motion.GeoPoint;

/**
 * A simple sample application that uses JXMapKit
 * 
 * @author Martin Steiger
 */
public class JxMap extends JFrame implements Runnable {

	private final JXMapKit jXMapKit = new JXMapKit();
	private final static String imagePath = "map/";
	private String curImageStr = null;
	private MyLabel picLabel = new MyLabel();

	public JxMap() {

		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		jXMapKit.setTileFactory(tileFactory);

		// location of Java
		final GeoPosition gp = new GeoPosition(1.299643, 103.787948);

		final JToolTip tooltip = new JToolTip();
		tooltip.setTipText("Java");
		tooltip.setComponent(jXMapKit.getMainMap());
		jXMapKit.getMainMap().add(tooltip);

		jXMapKit.setZoom(1);
		jXMapKit.setAddressLocation(gp);

		jXMapKit.getMainMap().addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// ignore
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				JXMapViewer map = jXMapKit.getMainMap();

				// convert to world bitmap
				Point2D worldPos = map.getTileFactory().geoToPixel(gp,
						map.getZoom());

				// convert to screen
				Rectangle rect = map.getViewportBounds();
				int sx = (int) worldPos.getX() - rect.x;
				int sy = (int) worldPos.getY() - rect.y;
				Point screenPos = new Point(sx, sy);

				// check if near the mouse
				if (screenPos.distance(e.getPoint()) < 20) {
					screenPos.x -= tooltip.getWidth() / 2;

					tooltip.setLocation(screenPos);
					tooltip.setVisible(true);
				} else {
					tooltip.setVisible(false);
				}
			}
		});

		// Display the viewer in a JFrame

		setTitle("JXMapviewer2 Example 6");
		getContentPane().add(jXMapKit);
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void setAddressLocation(double lat, double lon) {
		if (jXMapKit.getParent() == this) {
			jXMapKit.setAddressLocation(new GeoPosition(lat, lon));
		} else if (picLabel.getParent() == this) {

		}
	}

	public void dispImage(String file) {
		if (curImageStr == null) {
			curImageStr = file;
		} else if (curImageStr.compareTo(file) == 0) {
			return;
		}

		curImageStr = file;
		if (getContentPane().isAncestorOf(jXMapKit)) {
			getContentPane().remove(jXMapKit);
		}
		if (!getContentPane().isAncestorOf(picLabel)) {
			getContentPane().add(picLabel);
		}
		picLabel.setImage(imagePath + curImageStr);

	}

	public void setMarker(double lat, double lon) {
		picLabel.setMarker(lat, lon);
	}

	public void clearMarker() {
		picLabel.clearMarker();
	}

	public void hideImage() {
		curImageStr = null;

		if (getContentPane().isAncestorOf(picLabel)) {
			getContentPane().remove(picLabel);
		}
		if (!getContentPane().isAncestorOf(jXMapKit)) {
			getContentPane().add(jXMapKit);
		}
	}

}

class MyLabel extends JLabel {

	// double r = Math.toRadians(-23);
	double[] s = { 3111798, 3112660 };
	double[] ul = { 1.3004187, 103.7864650 };
	double[] lr = { 1.2981801, 103.7890214 };
	Dimension dim = new Dimension(7955, 6968);

	double[] marker = { Double.NaN, Double.NaN };

	private double graphicTranslateX = 0;
	private double graphicTranslateY = 0;
	private double graphicScale = 1;

	ImageIcon icon = null;

	public MyLabel() {
		Mouse m = new Mouse();

		this.addMouseListener(m);
		this.addMouseMotionListener(m);
		this.addMouseWheelListener(m);

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		AffineTransform tx = new AffineTransform();
		tx.translate(graphicTranslateX, graphicTranslateY);
		tx.scale(graphicScale, graphicScale);

		Graphics2D g2 = (Graphics2D) g;
		g2.setTransform(tx);

		double ratio = calcRatio(icon.getIconWidth(), icon.getIconHeight(),
				getWidth(), getHeight());
		int w = new Double(icon.getIconWidth() * ratio).intValue();
		int h = new Double(icon.getIconHeight() * ratio).intValue();
		int x = (getWidth() - w) / 2;
		int y = (getHeight() - h) / 2;
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.drawImage(icon.getImage(), x, y, w, h, null);

		if (!Double.isNaN(marker[0])) {
			g2.setColor(Color.RED);
			// g2.fillOval(x + 378, y + 187, 20, 20);
			g2.fillOval(x + (int) (marker[0] * ratio), y
					+ (int) (marker[1] * ratio), 20, 20);
			// 3783.9463679982564, 1876.0001819998631
		}
	}

	private double calcRatio(double sw, double sh, double dw, double dh) {
		double sr = sw / sh;
		double dr = dw / dh;
		if (sr > dr) {
			return dw / sw;
		} else {
			return dh / sh;
		}
	}

	public void setImage(String filename) {
		BufferedImage myPicture;
		try {
			myPicture = ImageIO.read(new File(filename));
			icon = new ImageIcon(myPicture);
			setIcon(icon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setMarker(double lat, double lon) {
		marker[0] = (lon - ul[1]) * s[0];
		marker[1] = (ul[0] - lat) * s[1];
		repaint();
	}

	public void clearMarker() {
		marker[0] = Double.NaN;
		marker[1] = Double.NaN;
	}

	class Mouse implements MouseListener, MouseMotionListener,
			MouseWheelListener {

		private int lastOffsetX;
		private int lastOffsetY;

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

				// make it a reasonable amount of zoom
				// .1 gives a nice slow transition
				double scaleChange = (.1 * e.getWheelRotation());
				graphicScale -= scaleChange;
				// don't cross negative threshold.
				// also, setting scale to 0 has bad effects
				graphicScale = Math.max(0.00001, graphicScale);

				graphicTranslateX += e.getX() * scaleChange;
				graphicTranslateY += e.getY() * scaleChange;

				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// new x and y are defined by current mouse location subtracted
			// by previously processed mouse location
			int newX = e.getX() - lastOffsetX;
			int newY = e.getY() - lastOffsetY;

			// increment last offset to last processed by drag event.
			lastOffsetX += newX;
			lastOffsetY += newY;

			// update the canvas locations
			graphicTranslateX += newX;
			graphicTranslateY += newY;

			// schedule a repaint.
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// capture starting point
			lastOffsetX = e.getX();
			lastOffsetY = e.getY();
		}

	}

}
