package com.astar.i2r.ins.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Polyline;

import com.astar.i2r.ins.localization.AccumulatedMotion;
import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.Reader;

public class IMUWorker extends SwingWorker<Pair<AccumulatedMotion, List<GPSData>>, Void> {

	private File xmlFile = null;
	private Pair<AccumulatedMotion, List<GPSData>> result = null;
	private AccumulatedMotion estimatedTrack = null;
	private List<GPSData> groundTruth = null;
	private MapView mapView = null;
	private JButton localizationButton = null;

	public IMUWorker(File imuIn, MainFrame _mainFrame) {
		xmlFile = imuIn;
		estimatedTrack = _mainFrame.estimatedTrack;
		groundTruth = _mainFrame.groundTruth;
		mapView = _mainFrame.mapView;
		localizationButton = _mainFrame.localizationButton;

	}

	@Override
	protected Pair<AccumulatedMotion, List<GPSData>> doInBackground() {

		Reader reader = new Reader(xmlFile);
		AccumulatedMotion track = new AccumulatedMotion();
		for (Data data : reader) {
			track.increment(data);
		}

		reader = new Reader(xmlFile, GPSData.NAME);
		List<GPSData> gpsdata = new ArrayList<GPSData>();
		for (Data data : reader) {
			gpsdata.add((GPSData) data);
		}

		return new Pair<AccumulatedMotion, List<GPSData>>(track, gpsdata);
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {
		try {
			result = get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		estimatedTrack = result.t;

		if (estimatedTrack != null) {
			// display estimatedTrack on the map

			Polyline estimatedTrackPolyline = loadTrack(estimatedTrack);
			BoundingBox bbox = MapView.getBoundingBox(estimatedTrackPolyline);
			estimatedTrackPolyline.setVisible(true, true);
			mapView.addLayer(estimatedTrackPolyline, bbox);
		}

		groundTruth = result.p;

		if (groundTruth != null) {
			Polyline groundTruthPolyline = loadTrack(groundTruth);
			BoundingBox bbox = MapView.getBoundingBox(groundTruthPolyline);
			groundTruthPolyline.setVisible(true, true);
			mapView.addLayer(groundTruthPolyline, bbox);

		}

		localizationButton.setEnabled(true);
	}

	private Polyline loadTrack(AccumulatedMotion list) {
		Polyline pl = new Polyline(MainFrame.IMUPAINT,
				MainFrame.GRAPHIC_FACTORY, true);
		List<LatLong> coordinateList = pl.getLatLongs();

		// add points into polyline
		for (int j = 0; j < list.size(); j++) {
			LatLong position = new LatLong(list.get(j).lat, list.get(j).lon);
			coordinateList.add(position);
		}
		return pl;
	}

	private Polyline loadTrack(List<GPSData> list) {
		Polyline pl = new Polyline(MainFrame.GPSPAINT,
				MainFrame.GRAPHIC_FACTORY, true);
		List<LatLong> coordinateList = pl.getLatLongs();

		// add points into polyline
		for (int j = 0; j < list.size(); j++) {
			LatLong position = new LatLong(list.get(j).gps[0],
					list.get(j).gps[1]);
			coordinateList.add(position);
		}
		return pl;
	}

}

class Pair<T, P> {
	T t;
	P p;

	public Pair(T _t, P _p) {
		// TODO Auto-generated constructor stub
		t = _t;
		p = _p;
	}
}
