package com.astar.i2r.ins.gui;

import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.localization.AccumulatedMotion;

public class PlayWorker extends SwingWorker<Void, Mat> {

	private static final Logger log = Logger.getLogger(PlayWorker.class
			.getName());

	// camera
	private VideoCapture camera = null;
	private VideoPanel panel = null;

	boolean paused = false;

	// videoPanel
	
	
	private AccumulatedMotion estimatedTrack = null;
	private List<GPSData> groundTruth = null;
	private MapView mapView = null;
	private FixedPixelCircle groundTruthMarker = null;
	private FixedPixelCircle estimatedTrackMarker = null;
	

	public PlayWorker(VideoCapture _camera, MainFrame _mainFrame) {
		// TODO Auto-generated constructor stub
		camera = _camera;
		panel = _mainFrame.videoPanel;

		estimatedTrack = _mainFrame.estimatedTrack;
		groundTruth = _mainFrame.groundTruth;
		mapView = _mainFrame.mapView;
		groundTruthMarker = _mainFrame.groundTruthMarker;
		estimatedTrackMarker = _mainFrame.estimatedTrackMarker;
	}

	@Override
	protected Void doInBackground() {
		// Double frameIntervalDouble = (double) (1 / 24); // CV_CAP_PROP_FPS=5
		// long frameIntervalLong = frameIntervalDouble.longValue();

		Mat frame = new Mat();
		while (!isCancelled()) {
			if (paused) {
				try {
					synchronized (this) {
						wait(1000);
					}
				} catch (InterruptedException ex) {
					log.fine("Background interrupted");
				}
			} else {

				camera.read(frame);
				if (!frame.empty()) {
					panel.updateImage(frame);
				} else {
					break;
				}

			}

		}

		return null;
	}

	@Override
	protected void process(List<Mat> chunks) {

	}

	public void pause() {
		paused = true;
	}

	public synchronized void resume() {
		paused = false;
		this.notify();
	}

	public boolean isPaused() {
		return this.paused;
	}

}
