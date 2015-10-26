package com.astar.i2r.ins.gui;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.overlay.Polyline;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.data.Reader;
import com.astar.i2r.ins.localization.AccumulatedMotion;
import com.astar.i2r.ins.localization.Localization;
import com.astar.i2r.ins.motion.GeoPosition;

public class LocalizationWorker extends SwingWorker<List<GeoPosition>, Void> {

	private File imuFile = null;
	private List<GeoPosition> track = null;
	private MapView mapView = null;
	private static String osmFile = "/home/ian/Documents/map/malaysia-singapore-brunei-latest.osm.pbf";

	public LocalizationWorker(File _imuFile, MainFrame _mainFrame) {
		// TODO Auto-generated constructor stub
		imuFile = _imuFile;
		mapView = _mainFrame.mapView;
		track = _mainFrame.calibratedTrack;
	}

	@Override
	protected List<GeoPosition> doInBackground() throws Exception {
		// TODO Auto-generated method stub
		Reader reader = new Reader(imuFile);
		Localization local = new Localization(osmFile, "graphFolder");
		for (Data data : reader) {
			local.increment(data);
		}
		return local.getTrack();
	}

	@Override
	protected void done() {
		// TODO Auto-generated method stub
		super.done();

		try {
			track = get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (track != null) {
			// display estimatedTrack on the map

			Polyline calibratedTrackPolyline = loadTrack(track);
			BoundingBox bbox = MapView.getBoundingBox(calibratedTrackPolyline);
			calibratedTrackPolyline.setVisible(true, true);
			mapView.addLayer(calibratedTrackPolyline, bbox);
		}

	}
	
	private Polyline loadTrack(List<GeoPosition> list) {
		Polyline pl = new Polyline(MainFrame.CALPAINT,
				MainFrame.GRAPHIC_FACTORY, true);
		List<LatLong> coordinateList = pl.getLatLongs();

		// add points into polyline
		for (int j = 0; j < list.size(); j++) {
			LatLong position = new LatLong(list.get(j).lat, list.get(j).lon);
			coordinateList.add(position);
		}
		return pl;
	}

}
