package com.astar.i2r.ins;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import org.gstreamer.Gst;
import org.mapsforge.map.reader.ReadBuffer;
import org.opencv.core.Core;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.gui.MainFrame;
import com.astar.i2r.ins.localization.Context;
import com.astar.i2r.ins.localization.Localization;
import com.astar.i2r.ins.map.MapWrapper;
import com.astar.i2r.ins.map.GeoMap;

/**
 * Hello world!
 *
 */
public class INS {

	private static final Logger log = Logger.getLogger(INS.class.getName());

	public static final int car0 = 0;
	
	// data queue
	private BlockingQueue<Data> dataQ = null;
	private Thread dSvr = null;
	private Thread rSvr = null;
	private Thread lSvr = null;
	private Map<Integer, Context> cList = null;

	/**
	 * Starts the {@code MapViewer}.
	 * 
	 * @param args
	 *            command line args: expects the map files as multiple
	 *            parameters.
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// args = Gst.init("Inertial Navigation", args);
		// Increase read buffer limit
		ReadBuffer.setMaximumBufferSize(6500000);

		String mapFiles = null;
		String vdoFile = null;
		String imuFile = null;

		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("m", "map", true,
				"The pathes and names of the Mapsforge Binary Map files");
		options.addOption("v", "vdo", true,
				"The path and name of the Video file.");
		options.addOption("i", "imu", true,
				"The path and name of the IMU file.");
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h"))
				help(options);
			if (cmd.hasOption("m")) {
				mapFiles = cmd.getOptionValue("m");
			}
			if (cmd.hasOption("v")) {
				vdoFile = cmd.getOptionValue("v");
			}
			if (cmd.hasOption("i")) {
				imuFile = cmd.getOptionValue("i");
			}
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help(options);
		}

	}

	public INS() {

		cList = new HashMap<Integer, Context>();
		dataQ = new LinkedBlockingQueue<Data>();

		GeoMap map = new MapWrapper();

		// Create TCP data server and request server
		dSvr = new DataServer(dataQ);
		rSvr = new RequestServer(cList, map);

		// Create server side localization worker
		lSvr = new Localization(dataQ, cList, map);

		// create server side GUI

		dSvr.start();
		rSvr.start();
		lSvr.start();
	}

	private static void help(Options options) {

		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);
		System.exit(0);

	}

}

class MyWindow implements Runnable {

	private String mapFiles = null;
	private String vdoFile = null;
	private String imuFile = null;

	public MyWindow(String _mapFiles, String _vdoFile, String _imuFile) {
		mapFiles = _mapFiles;
		vdoFile = _vdoFile;
		imuFile = _imuFile;
	}

	public void run() {
		try {
			MainFrame frame = new MainFrame(mapFiles, vdoFile, imuFile);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
