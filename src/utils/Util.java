package utils;

import chess.Piece;

import com.jme3.math.*;
import com.jme3.scene.*;

/**
 * @author Jim Fan  (c) 2014
 * Useful utility functions
 */
public class Util
{
	/**
	 * Convenient color generation
	 */
	public static ColorRGBA color(int R, int G, int B, double alpha)
	{
		return new ColorRGBA((float) (R/255.0), (float) (G/255.0), (float) (B/255.0), (float)alpha);
	}
	
	public static ColorRGBA color(int R, int G, int B)
	{
		return color(R, G, B, 1f);
	}
	
	/**
	 * Ugly way to retrieve color from a material config
	 */
	public static ColorRGBA getColor(Spatial spatial)
	{
		return (ColorRGBA) ((Geometry) spatial)
                        		.getMaterial().getParam("Color").getValue();
	}
	
	public static ColorRGBA getDiffuseColor(Piece piece)
	{
		return (ColorRGBA) (piece
                        		.getMaterial().getParam("Diffuse").getValue());
	}

	public static void setColor(Spatial spatial, ColorRGBA c)
	{
		((Geometry) spatial).getMaterial().setColor("Color", c);
	}
	
	/**
	 * Returns the radian representation of an angle
	 */
	public static float toRad(double deg) { return FastMath.DEG_TO_RAD * (float) deg; }

	/**
	 * Returns the degree representation of an angle
	 */
	public static float toDeg(double rad) { return (float) rad / FastMath.DEG_TO_RAD ; }
	
	/**
	 * Return the coordinate representation of a chess board square position (0 - 63)
	 * X: file, Y: rank
	 */
	public static int[] toXY(int pos)
	{
		return new int[] {pos & 7, pos >> 3};
	}
	
	/**
	 * rank + file coordinate to square
	 */
	public static int toSq(int file, int rank)
	{
		return (rank << 3) + file;  // y * 8 + 8
	}
	
	public static Vector3f toVec3f(double x, double y, double z)
	{
		return new Vector3f((float) x, (float) y, (float) z);
	}
}
