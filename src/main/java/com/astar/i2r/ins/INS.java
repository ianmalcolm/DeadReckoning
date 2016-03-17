package com.astar.i2r.ins;

import java.awt.EventQueue;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.astar.i2r.ins.map.GHMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.astar.i2r.ins.data.Data;
import com.astar.i2r.ins.data.TXTReaderServer;
import com.astar.i2r.ins.gui.ColoredWeightedWaypoint;
import com.astar.i2r.ins.gui.JxMap;
import com.astar.i2r.ins.localization.Localization;
import com.astar.i2r.ins.map.CarParkDB;

/**
 * Hello world!
 *
 */
public class INS {

	private static final Logger log = Logger.getLogger(INS.class.getName());

	// data queue
	private BlockingQueue<Data> dataQ = null;
	private BlockingQueue<ColoredWeightedWaypoint> GPSQ = null;

	private Thread dSvr = null;
	private Thread lSvr = null;

	/**
	 * Starts the {@code MapViewer}.
	 * 
	 * @param args
	 *            command line args: expects the map files as multiple
	 *            parameters.
	 */
	public static void main(String[] args) {

		String carparkmapdir = "map";
		String worldmap = "map/malaysia-singapore-brunei-latest.osm.pbf";
		// String imuFile = "sensor/1446089995.751188.txt";
		// String imuFile = "sensor/1446090161.558128.txt";

		// String imuFile = "sensor/1447040772.693506.txt";
		// String imuFile = "sensor/1448511685.792016.txt";
		String imuFile = "sensor/1448514394.581292.txt";

		// String imuFile = "sensor/1444893828.912658-withGT.txt";
		// String imuFile = "sensor/1446090536.171343-withGT.txt";
		// String imuFile = "sensor/1447040772.693506-withGT.txt";

		Options options = new Options();
		options.addOption("h", "help", false, "show help.");
		options.addOption("i", "imu", true,
				"The path and name of the IMU file.");
		options.addOption("b", "boundary", false,
				"Switch to DR mode when cross the boundary"
						+ " and enter the car park regardless of"
						+ " the strength of GPS signal.");
		options.addOption("c", "correction", false,
				"Correct the heading when enter the correction box.");
		options.addOption("g", "groundtruth", false,
				"Correct the localization status with groundtruth"
						+ " in data set.");
		options.addOption("s", "snap", false,
				"Snap the GPS coordinate to the road of the world map.");
		options.addOption("v", "verbose", false,
				"Print out the GPS coordinates drawed on the map.");
		options.addOption("d", "delay", true,
				"Add delay (ms) to slow the play of the track. 0 for no delay.");
		options.addOption("w", "worldmap", true,
				"specify the path to the worldmap other than Malaysia, Singapore and Brunei.");
		options.addOption("m", "map", true, "Specify the directory of maps.");

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("h"))
				help(options);
			if (cmd.hasOption("i")) {
				imuFile = cmd.getOptionValue("i");
			}
			if (cmd.hasOption("b")) {
				Localization.BOUNDARYFLAG = true;
			} else {
				Localization.BOUNDARYFLAG = false;
			}
			if (cmd.hasOption("c")) {
				Localization.CORRECTIONFLAG = true;
			} else {
				Localization.CORRECTIONFLAG = false;
			}
			if (cmd.hasOption("g")) {
				Localization.GROUNDTRUTHFLAG = true;
			} else {
				Localization.GROUNDTRUTHFLAG = false;
			}
			if (cmd.hasOption("v")) {
				Localization.VERBOSE = true;
			} else {
				Localization.VERBOSE = false;
			}
			if (cmd.hasOption("s")) {
				Localization.SNAPPEDGPS = true;
			} else {
				Localization.SNAPPEDGPS = false;
			}

			if (cmd.hasOption("m")) {
				carparkmapdir = cmd.getOptionValue("m");
			}
			if (cmd.hasOption("w")) {
				worldmap = cmd.getOptionValue("m");
			}
			if (cmd.hasOption("d")) {
				String delay = cmd.getOptionValue("d");
				TXTReaderServer.DELAY = Integer.parseInt(delay);
			} else {
				TXTReaderServer.DELAY = 0;
			}
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help(options);
		}

		CarParkDB.loadMap(carparkmapdir);
		new INS(new File(imuFile), worldmap);

	}

	public INS(File imu, String _worldmap) {
		
		GHMap ghmap = null;
		if (Localization.SNAPPEDGPS) {
			ghmap = new GHMap(_worldmap);
		}

		dataQ = new LinkedBlockingQueue<Data>();
		GPSQ = new LinkedBlockingQueue<ColoredWeightedWaypoint>();

		dSvr = new TXTReaderServer(imu, dataQ);
		lSvr = new Localization(dataQ, GPSQ, ghmap);
		JxMap window = new JxMap(GPSQ);

		dSvr.start();
		lSvr.start();
		EventQueue.invokeLater(window);

	}

	private static void help(Options options) {

		// This prints out some help
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);
		System.exit(0);

	}

}
