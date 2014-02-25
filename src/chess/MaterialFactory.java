package chess;

import utils.*;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

/**
 * @author Jim Fan  (c) 2014
 */
public class MaterialFactory
{
	private final AssetManager assetManager;
	// singleton instance
	private static MaterialFactory instance = null;
	
	private MaterialFactory(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}
	
	/**
	 * Should be called in the main class only once
	 */
	public static void setup(AssetManager assetManager)
	{
		if (instance == null)
			instance = new MaterialFactory(assetManager);
	}
	
	/**
	 * Singleton factory pattern
	 */
	public static MaterialFactory getInstance() {	return instance;	}
	
	// File locations
	private static final String 
		PHONG_SHADER = "MatDefs/Phong.j3md",
		PLAIN_SHADER = "MatDefs/Plain.j3md";
	
	/* Custom color repository */
	public static ColorRGBA Purple = Util.color(95, 0, 95);
	
	/* Texture Repository */
	public Material loadPlain(String texture, ColorRGBA color)
	{
		Material mat = new Material(assetManager, PLAIN_SHADER);
        mat.setColor("Color", color);
        if (texture != null)
            mat.setTexture("ColorMap",
            			assetManager.loadTexture("Textures/" + texture + ".jpg"));
        return mat;
	}
	
	public Material loadPlain(ColorRGBA color)
	{
		return loadPlain(null, color);
	}
	
	public Material loadMarble(ColorRGBA color)
	{
		Material mat = new Material(assetManager, PHONG_SHADER);
		mat.setBoolean("UseMaterialColors", true);
    	mat.setColor("Diffuse", color);
    	mat.setColor("Ambient", ColorRGBA.White);
    	mat.setColor("Specular", ColorRGBA.White);
    	mat.setFloat("Shininess", 60f);
    	mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Marble/black_marble.jpg"));
    	return mat;
	}
	
	public Material loadMarbleTransparent(ColorRGBA color)
	{
		Material mat = loadMarble(color);
		// PNG image with alpha channel
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Marble/black_marble.png"));
    	mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    	mat.getAdditionalRenderState().setAlphaTest(true);
    	// above alpha will be rendered
    	mat.getAdditionalRenderState().setAlphaFallOff(0f);
    	return mat;
	}
	
	/**
	 * portion < alphaThresh will not be rendered.
	 */
	public void setAlphaFallOff(Geometry obj, float alphaThresh)
	{
		obj.getMaterial().getAdditionalRenderState().setAlphaFallOff(alphaThresh);
	}
	
	/**
	 * Add this to RootNode to create a surrounding
	 * Must provide 6 textures with the proper naming
	 * @param nameTemplate will pad with _N _W _E _S _T (top) _B (bottom)
	 * @param ext extension of the image file
	 */
	public Spatial loadSkyBox(String nameTemplate, String ext)
	{
		return SkyFactory.createSky(assetManager, 
				assetManager.loadTexture("Textures/" + nameTemplate + "_W" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_E" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_N" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_S" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_T" + "." + ext), 
				assetManager.loadTexture("Textures/" + nameTemplate + "_B" + "." + ext));
	}
}
