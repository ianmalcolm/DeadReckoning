package com.astar.i2r.ins.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BoxLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
//import org.gstreamer.Bus;
//import org.gstreamer.GstObject;
//import org.gstreamer.State;
//import org.gstreamer.Tag;
//import org.gstreamer.TagList;
//import org.gstreamer.TagMergeMode;
//import org.gstreamer.elements.PlayBin;
//import org.gstreamer.swing.VideoComponent;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.awt.AwtGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.FileSystemTileCache;
import org.mapsforge.map.layer.cache.InMemoryTileCache;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.cache.TwoLevelTileCache;
import org.mapsforge.map.layer.debug.TileCoordinatesLayer;
import org.mapsforge.map.layer.debug.TileGridLayer;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.download.tilesource.TileSource;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.Model;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.opencv.highgui.VideoCapture;

import com.astar.i2r.ins.INS;
import com.astar.i2r.ins.data.GPSData;
import com.astar.i2r.ins.localization.AccumulatedMotion;
import com.astar.i2r.ins.motion.GeoPosition;

import java.awt.Component;

public class MainFrame extends JFrame implements ActionListener, ChangeListener {

	/**
	 * 
	 */
	private static final Logger log = Logger.getLogger(MainFrame.class
			.getName());
	private static final long serialVersionUID = 1L;
	static final GraphicFactory GRAPHIC_FACTORY = AwtGraphicFactory.INSTANCE;
	private static final boolean SHOW_DEBUG_LAYERS = false;
	private static final String LOADMAP = "LOADMAP";
	private static final String DISPMAP = "DISPMAP";
	private static final String LOADVDO = "LOADVDO";
	private static final String LOADIMU = "LOADIMU";
	private static final String ESTIMATETRACK = "CALCTRACK";
	private static final String LOCALIZATION = "LOCALIZATION";
	private static final String REWIND = "REWIND";
	private static final String FASTFORWARD = "FASTFORWARD";
	private static final String PLAY = "PLAY";
	private static final String STOP = "STOP";

	private JPanel contentPane = new JPanel();
	private JPanel algorithmPanel = new JPanel();
	private JPanel controlPanel = new JPanel();
	private JPanel displayPanel = new JPanel();
	private JPanel loadMapPanel = new JPanel();
	private JPanel loadVdoPanel = new JPanel();
	private JPanel loadIMUPanel = new JPanel();

	JButton dispMapButton = new JButton("Display Map");
	JButton estimateButton = new JButton("Load IMU");
	JButton localizationButton = new JButton("Localization");
	JButton playButton = new JButton("Play");

	VideoPanel videoPanel = new VideoPanel();

	JSlider progressBar = new JSlider();

	private JLabel loadMapLabel = new JLabel("Map:");
	private JTextField loadMapText = new JTextField();
	private JButton loadMapButton = new JButton("...");
	private JLabel loadVdoLabel = new JLabel("Video:");
	private JTextField loadVdoText = new JTextField();
	private JButton loadVdoButton = new JButton("...");
	private JLabel loadIMULabel = new JLabel("IMU:");
	private JTextField loadIMUText = new JTextField();
	private JButton loadIMUButton = new JButton("...");

	MapView mapView = createMapView();

	List<GPSData> groundTruth = null;
	AccumulatedMotion estimatedTrack = null;
	List<GeoPosition> calibratedTrack = null;
	FixedPixelCircle groundTruthMarker = null;
	FixedPixelCircle estimatedTrackMarker = null;

	static final Paint GPSPAINT = GRAPHIC_FACTORY.createPaint();
	static final Paint IMUPAINT = GRAPHIC_FACTORY.createPaint();
	static final Paint CALPAINT = GRAPHIC_FACTORY.createPaint();

	private final JPanel panel = new JPanel();
	private final JPanel statusPanel = new JPanel();
	private final Component rigidArea = Box
			.createRigidArea(new Dimension(0, 5));
	final JLabel lblTime = new JLabel("Time");

	PlayWorker playWorker = null;

	/**
	 * Create the frame.
	 */
	public MainFrame(String mapFiles, String vdoFile, String imuFile) {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(10, 10));
		setContentPane(contentPane);

		displayPanel.setLayout(new GridLayout(1, 2, 0, 0));

		displayPanel.add(mapView);
//		displayPanel.add(videoPanel);
		contentPane.add(displayPanel, BorderLayout.CENTER);

		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(controlPanel);

		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		loadMapPanel.setLayout(new BoxLayout(loadMapPanel, BoxLayout.X_AXIS));
		loadVdoPanel.setLayout(new BoxLayout(loadVdoPanel, BoxLayout.X_AXIS));
		loadIMUPanel.setLayout(new BoxLayout(loadIMUPanel, BoxLayout.X_AXIS));

		loadMapText.setColumns(10);
		loadMapText.setText(mapFiles);
		loadVdoText.setColumns(10);
		loadVdoText.setText(vdoFile);
		loadIMUText.setColumns(10);
		loadIMUText.setText(imuFile);
		loadMapLabel.setPreferredSize(new Dimension(50, loadMapLabel
				.getHeight()));
		loadVdoLabel.setPreferredSize(new Dimension(50, loadVdoLabel
				.getHeight()));
		loadIMULabel.setPreferredSize(new Dimension(50, loadIMULabel
				.getHeight()));

		loadMapButton.setActionCommand(LOADMAP);
		loadMapText.setActionCommand(DISPMAP);
		loadVdoButton.setActionCommand(LOADVDO);
		loadIMUButton.setActionCommand(LOADIMU);
		dispMapButton.setActionCommand(DISPMAP);
		estimateButton.setActionCommand(ESTIMATETRACK);
		localizationButton.setActionCommand(LOCALIZATION);
		playButton.setActionCommand(PLAY);

		loadMapButton.addActionListener(this);
		loadMapText.addActionListener(this);
		loadVdoButton.addActionListener(this);
		loadIMUButton.addActionListener(this);
		dispMapButton.addActionListener(this);
		estimateButton.addActionListener(this);
		localizationButton.addActionListener(this);
		playButton.addActionListener(this);

		loadMapPanel.add(loadMapLabel);
		loadMapPanel.add(loadMapText);
		loadMapPanel.add(loadMapButton);
		loadVdoPanel.add(loadVdoLabel);
		loadVdoPanel.add(loadVdoText);
		loadVdoPanel.add(loadVdoButton);
		loadIMUPanel.add(loadIMULabel);
		loadIMUPanel.add(loadIMUText);
		loadIMUPanel.add(loadIMUButton);

		controlPanel.add(statusPanel);
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

		statusPanel.add(lblTime);

		controlPanel.add(rigidArea);

		controlPanel.add(loadMapPanel);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		controlPanel.add(loadVdoPanel);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		controlPanel.add(loadIMUPanel);
		controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		controlPanel.add(progressBar);
		progressBar.setEnabled(false);
		progressBar.addChangeListener(this);
		panel.add(algorithmPanel);
		algorithmPanel.setPreferredSize(new Dimension(100, algorithmPanel
				.getHeight()));
		algorithmPanel
				.setLayout(new BoxLayout(algorithmPanel, BoxLayout.Y_AXIS));

		algorithmPanel.add(dispMapButton);
		algorithmPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		algorithmPanel.add(estimateButton);
		algorithmPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		algorithmPanel.add(localizationButton);
		algorithmPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		algorithmPanel.add(playButton);

		GPSPAINT.setColor(Color.BLUE);
		GPSPAINT.setStrokeWidth(8);
		GPSPAINT.setStyle(Style.STROKE);
		IMUPAINT.setColor(Color.RED);
		IMUPAINT.setStrokeWidth(8);
		IMUPAINT.setStyle(Style.STROKE);
		CALPAINT.setColor(Color.GREEN);
		CALPAINT.setStrokeWidth(8);
		CALPAINT.setStyle(Style.STROKE);

		PreferencesFacade preferencesFacade = new JavaUtilPreferences(
				Preferences.userNodeForPackage(INS.class));
		final Model model = mapView.getModel();
		model.init(preferencesFacade);

		this.addWindowListener(new WindowCloseDialog(this, mapView,
				preferencesFacade));
		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {

		int returnVal = 0;
		JFileChooser fc = null;

		String cmd = e.getActionCommand();
		switch (cmd) {
		case LOADMAP: {

			// choose the file
			fc = new JFileChooser();
			fc.setMultiSelectionEnabled(true);
			returnVal = fc.showOpenDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				break;
			}
			File[] files = fc.getSelectedFiles();
			List<String> fileList = new ArrayList<String>();
			for (File file : files) {
				String base = System.getProperty("user.dir");
				String fullpath = file.getAbsolutePath();
				String relativePath = new File(base).toURI()
						.relativize(new File(fullpath).toURI()).getPath();
				fileList.add(relativePath);
			}

			loadMapText.setText(StringUtils.join(fileList, ","));
		}
		case DISPMAP: {
			String[] filenames = StringUtils.split(loadMapText.getText(), ",");

			List<File> fileList = new ArrayList<File>();
			for (String filename : filenames) {
				File file = new File(filename);
				fileList.add(file);
			}

			// loadMapText.setText(fc.get);

			// display the map
			BoundingBox bbox = addLayers(mapView, fileList);
			mapView.setMapPosition(bbox);
		}

			break;
		case LOADVDO: {
			fc = new JFileChooser();
			returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				loadVdoText.setText(file.getAbsolutePath());
			}
			break;

		}
		case LOADIMU: {
			fc = new JFileChooser();
			returnVal = fc.showOpenDialog(this);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				break;
			}
			File file = fc.getSelectedFile();
			loadIMUText.setText(file.getAbsolutePath());
			break;
		}
		case ESTIMATETRACK: {
			File file = new File(loadIMUText.getText());
			IMUWorker imuWorker = new IMUWorker(file, this);
			imuWorker.execute();
			break;
		}
		case LOCALIZATION: {
//			new LocalizationWorker(this);
//			fc = new JFileChooser();
//			returnVal = fc.showOpenDialog(this);
//			if (returnVal != JFileChooser.APPROVE_OPTION) {
//				break;
//			}
//			File file = fc.getSelectedFile();
//			System.out.println(file.getAbsolutePath());
			File file = new File(loadIMUText.getText());
			LocalizationWorker localizationWorker = new LocalizationWorker(file, this);
			localizationWorker.execute();
			break;
		}
		case REWIND:
			break;
		case PLAY: {

			if (playWorker == null) {
				VideoCapture cap = new VideoCapture(loadVdoText.getText());
				if (!cap.isOpened()) {
					log.warning("Cannot open video file!");
					break;
				}

				playWorker = new PlayWorker(cap, this);
				playWorker.execute();
			} else {
				if (!playWorker.isCancelled()) {
					if (!playWorker.isPaused()) {
						playWorker.pause();
					} else {
						playWorker.resume();
					}
				}
			}
		}
			break;
		case FASTFORWARD:
			break;
		case STOP:
			break;
		default:
		}

	}

	private MapView createMapView() {
		MapView mapView = new MapView();
		mapView.getMapScaleBar().setVisible(true);
		if (SHOW_DEBUG_LAYERS) {
			mapView.getFpsCounter().setVisible(true);
		}
		mapView.addComponentListener(new MapViewComponentListener(mapView,
				mapView.getModel().mapViewDimension));

		MouseEventListener mouseEventListener = new MouseEventListener(
				mapView.getModel());
		mapView.addMouseListener(mouseEventListener);
		mapView.addMouseMotionListener(mouseEventListener);
		mapView.addMouseWheelListener(mouseEventListener);

		return mapView;
	}

	private BoundingBox addLayers(MapView mapView, List<File> mapFiles) {
		Layers layers = mapView.getLayerManager().getLayers();

		// layers.add(createTileDownloadLayer(tileCache,
		// mapView.getModel().mapViewPosition));
		BoundingBox result = null;
		for (int i = 0; i < mapFiles.size(); i++) {
			File mapFile = mapFiles.get(i);
			TileRendererLayer tileRendererLayer = createTileRendererLayer(
					createTileCache(i), mapView.getModel().mapViewPosition,
					true, true, mapFile);
			BoundingBox boundingBox = tileRendererLayer.getMapDataStore()
					.boundingBox();
			result = result == null ? boundingBox : result.extend(boundingBox);
			layers.add(tileRendererLayer);
		}
		if (SHOW_DEBUG_LAYERS) {
			layers.add(new TileGridLayer(GRAPHIC_FACTORY,
					mapView.getModel().displayModel));
			layers.add(new TileCoordinatesLayer(GRAPHIC_FACTORY, mapView
					.getModel().displayModel));
		}
		return result;
	}

	private TileCache createTileCache(int index) {
		TileCache firstLevelTileCache = new InMemoryTileCache(128);
		File cacheDirectory = new File(System.getProperty("java.io.tmpdir"),
				"mapsforge" + index);
		TileCache secondLevelTileCache = new FileSystemTileCache(1024,
				cacheDirectory, GRAPHIC_FACTORY);
		return new TwoLevelTileCache(firstLevelTileCache, secondLevelTileCache);
	}

	@SuppressWarnings("unused")
	private Layer createTileDownloadLayer(TileCache tileCache,
			MapViewPosition mapViewPosition) {
		TileSource tileSource = OpenStreetMapMapnik.INSTANCE;
		TileDownloadLayer tileDownloadLayer = new TileDownloadLayer(tileCache,
				mapViewPosition, tileSource, GRAPHIC_FACTORY);
		tileDownloadLayer.start();
		return tileDownloadLayer;
	}

	private TileRendererLayer createTileRendererLayer(TileCache tileCache,
			MapViewPosition mapViewPosition, boolean isTransparent,
			boolean renderLabels, File mapFile) {
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				new MapFile(mapFile), mapViewPosition, isTransparent,
				renderLabels, GRAPHIC_FACTORY);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
		return tileRendererLayer;
	}

	// private List<File> getMapFiles(String[] args) {
	// if (args.length == 0) {
	// throw new IllegalArgumentException("missing argument: <mapFile>");
	// }
	//
	// List<File> result = new ArrayList<>();
	// for (String arg : args) {
	// File mapFile = new File(arg);
	// if (!mapFile.exists()) {
	// throw new IllegalArgumentException("file does not exist: "
	// + mapFile);
	// } else if (!mapFile.isFile()) {
	// throw new IllegalArgumentException("not a file: " + mapFile);
	// } else if (!mapFile.canRead()) {
	// throw new IllegalArgumentException("cannot read file: "
	// + mapFile);
	// }
	// result.add(mapFile);
	// }
	// return result;
	// }

	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		JSlider source = (JSlider) arg0.getSource();
		if (!source.getValueIsAdjusting()) {
			int num = (int) source.getValue();
			double[] cuPosArray = groundTruth.get(num).gps;
			LatLong curPos = new LatLong(cuPosArray[0], cuPosArray[1]);
			groundTruthMarker.setLatLong(curPos);
			groundTruthMarker.requestRedraw();
			lblTime.setText("Time: " + groundTruth.get(num).time.toString());
		}

	}

}
