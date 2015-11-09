package com.astar.i2r.ins.motion;

import java.awt.geom.Point2D;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.Ignore;
import org.junit.Test;

//<data relatedTime="518915.564" sensorType="GPS">
//	<Lat>1.298202</Lat>
//	<Lon>103.788125</Lon>
//	<HorAcc>10.000000</HorAcc>
//	<VerAcc>6.000000</VerAcc>
//	<Spd>4.760000</Spd>
//</data>
//<data relatedTime="518916.41" sensorType="Motion">
//	<GraX>-0.989206</GraX>
//	<GraY>0.030727</GraY>
//	<GraZ>0.143273</GraZ>
//	<AccX>-0.012060</AccX>
//	<AccY>0.035145</AccY>
//	<AccZ>-0.004540</AccZ>
//	<Roll>2.773741</Roll>
//	<Pitch>-1.251804</Pitch>
//	<Yaw>2.874451</Yaw>
//	<RelRoll>-1.714632</RelRoll>
//	<RelPitch>-0.030732</RelPitch>
//	<RelYaw>-1.910005</RelYaw>
//	<RotRatX>-0.020661</RotRatX>
//	<RotRatY>-0.024347</RotRatY>
//	<RotRatZ>-0.027376</RotRatZ>
//	<MagX>0.000000</MagX>
//	<MagY>0.000000</MagY>
//	<MagZ>0.000000</MagZ>
//	<MagAcc>-1</MagAcc>
//</data>

public class AttitudeTest {

	@Ignore
	@Test
	public void test() {
		// double[] cardanx90 = { 3.1415926 / 2, 0, 0 };
		// double[] cardany90 = { 0, 3.1415926 / 2, 0 };
		// double[] cardan = { -1.714632, -0.030732, -1.910005 };
		double[] cardan = { -1.714632, -0.030732, -1.910005 };
		// double[] cardan = { 0, 0, 0 };
		// ZEROPOINTVECTOR;

		// Double roll = FastMath.toDegrees(cardan[0]); // y
		// Double pitch = FastMath.toDegrees(cardan[1]); // x
		// Double yaw = FastMath.toDegrees(cardan[2]); // z
		// System.out.println(roll.toString() + ',' + pitch.toString() + ','
		// + yaw.toString());
		//
		// Attitude att = new Attitude(cardan, 100);
		// Vector3D worldVel = att.getVelocity(10);
		// System.out.println(worldVel.toString());
		//
		// Rotation r = new Rotation(RotationOrder.ZXZ, cardan[2], cardan[0],
		// cardan[1]);
		// worldVel = r.applyTo(new Vector3D(0, 0, -1));
		// System.out.println(worldVel.scalarMultiply(10).toString());

		//
		// Attitude att = new Attitude(cardan, 100);
		// Rotation calib = new Rotation(RotationOrder.ZXZ, 0, 0, Math.PI/2);
		// Rotation zerocalib = new Rotation(RotationOrder.ZXZ, 0, 0, 0);
		// Attitude newatt = att.calibrate(calib);
		// Attitude zeroatt = att.calibrate(zerocalib);
		// System.out.println(att.toString());
		// System.out.println(zeroatt.toString());
		// System.out.println(newatt.toString());

		GeoPoint p = new GeoPoint(1.298167, 103.788173, 0, 0);
		// GeoPosition q = new GeoPosition(1.300103, 103.790265, 0, 0);
		// GeoPosition r = new GeoPosition(1.299014, 103.790849, 0, 0);

		GeoPoint s = new GeoPoint(1.298167, 103.790265, 0, 0);
		GeoPoint t = new GeoPoint(1.300103, 103.788173, 0, 0);

		Vector3D u = new Vector3D(1, 0, 0);
		Vector3D v = new Vector3D(0, 1, 0);

		System.out.println(u.getAlpha() + "\t" + v.getAlpha());
		System.out.println(GeoPoint.distance(p, s).getAlpha() + "\t"
				+ GeoPoint.distance(p, t).getAlpha());

		GeodeticCalculator calc = new GeodeticCalculator();

		calc.setStartingGeographicPoint(103.788173, 1.298167);
		calc.setDirection(90, 100);

		Point2D dest = calc.getDestinationGeographicPoint();

		System.out.println(dest.toString());

	}

	@Test
	public void attitudetest() {
		// double[] cardan = { -1.579483, -0.005706, -3.056576 };
		// Attitude attitude = new Attitude(cardan, -0.6289496183304188, 0);
		double[] cardan = { -1.579483, 0, -3.056576 };
		Attitude attitude = new Attitude(cardan, 0, 0);
		Vector3D vel = attitude.getVelocity(0.44);
		System.out.println(vel.toString());
		// Velocity approxVel = new Velocity(vel, data.time);

		GeoPoint p = new GeoPoint(1.297446, 103.789893, 0, 0);
		GeoPoint q = new GeoPoint(1.298639, 103.787110, 0, 0);
		Vector3D v = GeoPoint.distance(p, q);

		System.out.println(v.getAlpha() + Math.PI / 2);

	}
	
}
