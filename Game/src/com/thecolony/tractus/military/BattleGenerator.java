package com.thecolony.tractus.military;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.thecolony.tractus.graphics.GraphicsManager;
import com.thecolony.tractus.military.battle.FlotillaBattler;
import com.thecolony.tractus.military.ships.Flotilla;
import com.thecolony.tractus.military.ships.Ship;
import com.thecolony.tractus.player.Player;
import com.thecolony.tractus.saveInfo.Filer;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by wes on 12/9/14.
 */
public class BattleGenerator {
    private static Node rootNode, loneShipsNode, flotillasNode;
    private static ArrayList<Ship> loneShips;
    private static ArrayList<Flotilla> flotillas;
    private static ArrayList<FlotillaBattler> flotillaBattles;
    private static double[] stats;
    private static Filer filer;
    public static void loadBattlers(Node rootNode, Node loneShipsNode, ArrayList<Ship> ships, Node flotillasNode, ArrayList<Flotilla> flotillas, ArrayList<FlotillaBattler> battles, double[] stats, Filer filer){
        BattleGenerator.stats=stats;
        BattleGenerator.rootNode=rootNode;
        BattleGenerator.loneShipsNode=loneShipsNode;
        BattleGenerator.loneShips=ships;
        BattleGenerator.flotillasNode=flotillasNode;
        BattleGenerator.flotillas=flotillas;
        BattleGenerator.flotillaBattles=battles;
        BattleGenerator.filer=filer;
        makeShips();
        makeFlotillas();
    }
    private static Ship generateShip(int player, Node node, int num, double[] stats) {
        return generateShip(player, node, num, Vector3f.ZERO, stats);
    }

    private static Ship generateShip(int player, Node node, int num, Vector3f pos, double[] stats) {
        return generateShip(player, node, num, Ship.SHIP_TYPE.Fighter, pos, stats);
    }

    private static Ship generateShip(int player, Node node, int num, Ship.SHIP_TYPE type, double[] stats) {
        return generateShip(player, node, num, type, Vector3f.ZERO, stats);
    }

    private static Ship generateShip(int player, Node node, int num, Ship.SHIP_TYPE type, Vector3f pos, double[] stats) {
        Player playah = new Player(player);
        Ship shit = new Ship(playah, type, type.toString() + num, node, pos, stats, 0, 0, 0, 0.0);
        shit.getMoveableObject3d().getModel().setMaterial(GraphicsManager.generateMaterial(shit.getPlayer().getColor()));
        return shit;
    }
    private static void makeShips()
    {
        loneShips = new ArrayList<Ship>();

        for (int i = 0; i < 5; i++)
        {
            loneShips.add(generateShip(3, loneShipsNode, i, new Vector3f(0.0f, 0.0f, -(30 + i * 3)), stats));
        }
    }
    private static void addShips(Ship s){
        Element ship=filer.addObject("ship","name",s.getName());
        String type="?";
        switch (s.getType()) {
            case Fighter: type="Fi";
                break;
            case Frigate: type="Fr";
                break;
            case Cruiser: type="Cr";
                break;
            case CapitalShip: type="Ca";
                break;
        }
        String stat="";
        for(double d:stats) stat+=d;
        String loc=s.getPosition().getX()+",0,"+s.getPosition().getZ();
        String node=s.getMoveableObject3d().getNode().toString();
        filer.addInfo(ship,"type",""+type);
        filer.addInfo(ship,"player",""+s.getPlayer().toString());
        filer.addInfo(ship,"node",""+node);
        filer.addInfo(ship,"loc",""+loc);
        filer.addInfo(ship,"stats",""+stat);
        filer.addInfo(ship,"cost","0");
        filer.addInfo(ship,"crew","0");
        filer.addInfo(ship,"ammo","0");
        filer.addInfo(ship,"fuel","0");
    }
    private static void makeFlotillas(){
        Ship[] ships1 = new Ship[25];
        for (int i = 0; i < ships1.length; i++)
        {
            ships1[i] = generateShip(0, flotillasNode, i, stats);
        }

        Ship[] ships2 = new Ship[9];
        for (int i = 0; i < ships2.length; i++)
        {
            ships2[i] = generateShip(1, flotillasNode, i, Ship.SHIP_TYPE.CapitalShip, stats);
        }

        Ship[] ships3 = new Ship[25];
        for (int i = 0; i < ships3.length; i++)
        {
            ships3[i] = generateShip(2, flotillasNode, i, stats);
        }

        flotillas = new ArrayList<Flotilla>();
        flotillas.add(new Flotilla(ships1, false, new Vector3f(-50.0f, 0.0f, 100.0f), "Flotilla 1"));
        flotillas.add(new Flotilla(ships2, false, new Vector3f(50.0f, 0.0f, 0.0f), "Flotilla 2"));
        flotillas.add(new Flotilla(ships3, false, new Vector3f(100.0f, 0.0f, 25.0f), "Flotilla 3"));
        flotillaBattles = new ArrayList<FlotillaBattler>();
    }
    private static void addFlotillas(Flotilla f){
        Element flotilla=filer.addObject("flotilla","name",f.getName());
        String ship="";
        for(Ship s:f.getFlotilla()) ship+=s.getName();
        String loc=f.getPosition().getX()+",0,"+f.getPosition().getZ();
        filer.addInfo(flotilla,"ships",""+ship);
        filer.addInfo(flotilla,"loc",""+loc);
    }
    private static void addFlotillaBattler(FlotillaBattler f){
        Element flotillaBattle=filer.addObject("flotillaBattles","name","");
        filer.addInfo(flotillaBattle,"attacker",""+f.getAttacker().toString());
        filer.addInfo(flotillaBattle,"defender",""+f.getDefender().toString());
    }
}
