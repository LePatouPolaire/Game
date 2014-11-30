package com.thecolony.tractus.graphics.threedmovement.drawableobjects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 * A class that holds all of the model's as Spatials.
 * @author Joe Pagliuco
 */
public abstract class GameModels
{
    private static Spatial M_SHIP_FIGHTER;
    private static Spatial M_SHIP_FRIGATE;
    private static Spatial M_SHIP_CRUISER;
    private static Spatial M_SHIP_CAPITAL_SHIP;
    
    private static Spatial M_OBJECT_SELECTED;
    private static Spatial M_OBJECT_SELECTED_2;
    
    public static void loadModels(AssetManager contentMan)
    {        
        Material mat = new Material(contentMan, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Red);
        mat.setColor("Diffuse", ColorRGBA.Red);
        M_SHIP_FIGHTER = contentMan.loadModel("Models/fighter.obj");
        M_SHIP_FIGHTER.setMaterial(mat.clone());
        M_SHIP_FIGHTER.scale(0.25f);
        
        M_SHIP_CAPITAL_SHIP = contentMan.loadModel("Models/Cube.001.mesh.xml");
        M_SHIP_CAPITAL_SHIP.setMaterial(new Material(contentMan, "Common/MatDefs/Misc/Unshaded.j3md"));
        M_SHIP_CAPITAL_SHIP.scale(0.5f);
        
        mat.setColor("Ambient", ColorRGBA.Yellow);
        mat.setColor("Diffuse", ColorRGBA.Yellow);
        M_OBJECT_SELECTED = contentMan.loadModel("Models/selected.obj");
        M_OBJECT_SELECTED.setMaterial(mat.clone());
        M_OBJECT_SELECTED.scale(0.25f);
        
        mat.setColor("Ambient", ColorRGBA.Gray);
        mat.setColor("Diffuse", ColorRGBA.Gray);
        M_OBJECT_SELECTED_2 = contentMan.loadModel("Models/selected2.obj");
        M_OBJECT_SELECTED_2.setMaterial(mat.clone());
    }
    
    public static Spatial getShipFighterModel()
    {
        return M_SHIP_FIGHTER.clone();
    }
    public static Spatial getShipFrigateModel()
    {
        return M_SHIP_FRIGATE.clone();
    }
    public static Spatial getShipCruiserModel()
    {
        return M_SHIP_CRUISER.clone();
    }
    public static Spatial getShipCaptialShipModel()
    {
        return M_SHIP_CAPITAL_SHIP.clone();
    }
    
    public static Spatial getSelectedObjectModel()
    {
        return M_OBJECT_SELECTED.clone();
    }
    public static Spatial getSelectedObjectModel2()
    {
        return M_OBJECT_SELECTED_2.clone();
    }
}