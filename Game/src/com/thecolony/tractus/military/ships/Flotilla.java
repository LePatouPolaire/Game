/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thecolony.tractus.military.ships;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.thecolony.tractus.audio.AudioManager;
import com.thecolony.tractus.game.Game;
import com.thecolony.tractus.graphics.GraphicsManager;
import com.thecolony.tractus.graphics.drawableobjects.DrawableObject3d;
import com.thecolony.tractus.military.battle.BattleObject;
import static com.thecolony.tractus.military.battle.BattleObject.BATTLE_STAT_MOVEMENT_SPEED;
import com.thecolony.tractus.military.battle.MoveableBattleObject;

/**
 *
 * @author Mark Haynie
 * @author Joe Pagliuco
 */
public class Flotilla extends MoveableBattleObject
{
    private static final float BATTLE_TIME_FACTOR = 0.05f;
    
    private Ship[] flotilla;    
    private boolean isFull;
    
    private int worth;
    
    private Vector3f centerPosition;
    private Geometry wireBoxGeometry;
    private Vector3f targetPoint;
    
    /**
     * NOT ACTUALLY DRAWN, USED TO GET DISPLAY INFO.
     */
    private DrawableObject3d model;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START CONSTRUCTORS /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Flotilla()
    {
        initialize(new Ship[0], Vector3f.ZERO, false, "Empty Name", true);
    }

    public Flotilla(Ship[] group, boolean isFull, Vector3f centerPosition, String name)
    {
        initialize(group, centerPosition, isFull, name, true);
    }
    
    public Flotilla(Ship[] group, boolean isFull, Vector3f centerPosition, String name, boolean setPositions)
    {
        initialize(group, centerPosition, isFull, name, setPositions);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END CONSTRUCTORS ///////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START INITIALIZATION METHODS ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initialize(Ship[] group, Vector3f centerPosition, boolean isFull, String name, boolean setPositions)
    {
        flotilla = group;
        this.centerPosition = centerPosition;
        this.isFull = isFull;

        if (setPositions && flotilla.length != 0)
            setShipPositions();

        this.name = name;

        setMovementSpeed();
        
        setDisplayInfo();
    }

    private void setShipPositions()
    {
        // Temporary way to do it...
        int sideLength = (int) Math.sqrt(flotilla.length);
        if (sideLength * sideLength != flotilla.length)
        {
            sideLength++;
        }

        float maxExtent = 0;
        for (int i = 0; i < flotilla.length; i++)
        {
            BoundingBox b = (BoundingBox) flotilla[i].getMoveableObject3d().getModel().getWorldBound();
            maxExtent = Math.max(maxExtent, b.getXExtent());
            maxExtent = Math.max(maxExtent, b.getZExtent());
        }
        maxExtent *= 2.0f * 1.5f;
        
        int halfSideLength = sideLength >> 1;
        int offset = (sideLength % 2 == 0) ? 0 : 1;
        int index = 0;
        for (int i = -halfSideLength; i < halfSideLength + offset; i++)
        {
            boolean stop = false;
            for (int j = -halfSideLength; j < halfSideLength + offset; j++)
            {
                flotilla[index++].getMoveableObject3d().getModel().setLocalTranslation(centerPosition.add(new Vector3f(maxExtent, 0.0f, maxExtent).mult(new Vector3f(i, 1, j))));                
                stop = index == flotilla.length;
                if (stop)
                    break;
            }
            if (stop)
                break;
        }

        float height = 0.0f;
        for (int i = 0; i < flotilla.length; i++)
            height = Math.max(height, ((BoundingBox) flotilla[i].getMoveableObject3d().getModel().getWorldBound()).getYExtent());
        height *= 2.0f;
        
        Vector3f min = flotilla[0].getPosition();
        BoundingBox b = (BoundingBox) flotilla[0].getMoveableObject3d().getModel().getWorldBound();
        min.add(b.getXExtent(), b.getYExtent(), b.getZExtent());
        min.add(10, 10, 10);
        
        b = (BoundingBox) flotilla[flotilla.length - 1].getMoveableObject3d().getModel().getWorldBound();
        Vector3f max = flotilla[flotilla.length - 1].getPosition();
        max.add(b.getXExtent(), b.getYExtent(), b.getZExtent());
        
        BoundingBox boundingBox = new BoundingBox(min, max.add(0, height, 0));
        WireBox wireBox = new WireBox();
        wireBox.fromBoundingBox(boundingBox);
        wireBoxGeometry = new Geometry("Flotilla WireBox Geometry", wireBox);
        wireBoxGeometry.setMaterial(GraphicsManager.getDefaultWhiteMaterial());
        wireBoxGeometry.setLocalTranslation(centerPosition);
        model = new DrawableObject3d(name, new Node(), wireBoxGeometry.clone(), getCenterPosition());
    }

    private void setMovementSpeed()
    {
        if (flotilla.length == 0)
            return;
        if (flotilla.length > 0)
        {
            qualities[BATTLE_STAT_MOVEMENT_SPEED] = (float) flotilla[0].getBattleStat(BattleObject.BATTLE_STAT_MOVEMENT_SPEED);
            for (int i = 1; i < flotilla.length; i++)
                qualities[BATTLE_STAT_MOVEMENT_SPEED] = (float) Math.min(getBattleStat(BATTLE_STAT_MOVEMENT_SPEED), flotilla[i].getBattleStat(BattleObject.BATTLE_STAT_MOVEMENT_SPEED));
        }
    }
    
    private void setDisplayInfo()
    {
        String[] display = new String[]
        {
            "Flotilla:",
            " Name: " + name,
            " Battle Stats",
            "  HP: " + String.format("%.2f", getFlotillaStat(BattleObject.BATTLE_STAT_HP)),
            "  Reg Power: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_POWER),
            "  Sp Power: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_POWER),
            "  Reg Defense: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_DEFENSE),
            "  Sp Defense: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_DEFENSE),
            "  Reg Attack Cooldown: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ATTACK_COOLDOWN),
            "  Sp Attack Cooldown: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ATTACK_COOLDOWN),
            "  Reg Accuracy: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ACCURACY),
            "  Sp Accuracy: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ACCURACY),
            "  Reg Range: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_RANGE),
            "  Sp Range: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_RANGE),
            "  Reg Armor Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ARMOR_STAT),
            "  Sp Armor Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ARMOR_STAT),
            "  Reg Weapon Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_WEAPON_STAT),
            "  Sp Weapon Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_WEAPON_STAT),
            "  Repair Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_REPAIR_ABILITY),
            "  Transport Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_TRANSPORT_ABILITY),
            "  Build Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_BUILD_ABILITY),
            "  Movement Speed: " + getBattleStat(BATTLE_STAT_MOVEMENT_SPEED)
        };
        
        model.getModel().setUserData("Display Info", Game.hashtagFixMyStringGurl(display));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END INITIALIZATION METHODS /////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START UPDATE METHODS ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Update the flotilla.
     *
     * @param deltaTime The time elapsed between each frame.
     */
    public void update(float deltaTime)
    {
        for (int i = 0; i < flotilla.length; i++)
        {
            flotilla[i].update(deltaTime);
        }

        if (isMoving() && !isRotating())
        {
            Vector3f direction = targetPoint.subtract(centerPosition).normalize();
            wireBoxGeometry.move(direction.mult((float)getBattleStat(BATTLE_STAT_MOVEMENT_SPEED) * deltaTime));
            centerPosition.addLocal(direction.mult((float)getBattleStat(BATTLE_STAT_MOVEMENT_SPEED) * deltaTime));
            model.getModel().move(direction.mult((float)getBattleStat(BATTLE_STAT_MOVEMENT_SPEED) * deltaTime));
        }
        
        setDisplayInfo();
    }

    public void move(Vector3f offset)
    {
        for (int i = 0; i < flotilla.length; i++)
        {
            flotilla[i].move(offset);
        }
        centerPosition.addLocal(offset);
        wireBoxGeometry.move(offset);
        model.getModel().move(offset);
    }

    public void sortByHP()
    {
        Ship[] temp = new Ship[flotilla.length];
        Ship[] temp2 = flotilla;
        for (int j = 0; j < flotilla.length; j++)
        {
            int place = 0;
            double smallest = temp2[place].getBattleStat(BattleObject.BATTLE_STAT_HP);
            for (int i = 0; i < temp2.length; i++)
            {
                if (temp2[i].getBattleStat(BattleObject.BATTLE_STAT_HP) < smallest)
                {
                    place = i;
                }
            }
            temp[j] = temp2[place];
            Ship temp3 = temp2[temp2.length - 1];
            temp2[place] = temp3;
            Ship[] temp4 = temp2;
            temp2 = new Ship[temp2.length - 1];
            for (int a = 0; a < temp2.length; a++)
            {
                temp2[a] = temp4[a];
            }
        }
        flotilla = temp;
    }

    public void sortByAttack()
    {
        Ship[] temp = new Ship[flotilla.length];
        Ship[] temp2 = flotilla;
        for (int j = 0; j < flotilla.length; j++)
        {
            int place = 0;
            double smallest = temp2[place].getBattleStat(BattleObject.BATTLE_STAT_REG_POWER);
            for (int i = 0; i < temp2.length; i++)
            {
                if (temp2[i].getBattleStat(BattleObject.BATTLE_STAT_REG_POWER) > smallest)
                {
                    place = i;
                }
            }
            temp[j] = temp2[place];
            Ship temp3 = temp2[temp2.length - 1];
            temp2[place] = temp3;
            Ship[] temp4 = temp2;
            temp2 = new Ship[temp2.length - 1];
            for (int a = 0; a < temp2.length; a++)
            {
                temp2[a] = temp4[a];
            }
        }
        flotilla = temp;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START BATTLE METHODS ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void checkRemoveShip()
    {
        if (flotilla.length > 1)
        {
            Ship[] temp = flotilla;
            for (int i = 0; i < temp.length; i++)
            {
                if (temp[i].getBattleStat(BattleObject.BATTLE_STAT_HP) == 0)
                {
		AudioManager.getShipDeath().playInstance();
                    flotilla[i].getMoveableObject3d().changeNodeState(false);

                    boolean passed = false;
                    Ship[] temp2 = new Ship[temp.length - 1];
                    for (int j = 0; j < temp.length; j++)
                    {
                        if (j == i)
                        {
                            j++;
                            passed = true;
                        }
                        if (passed)
                        {
                            temp2[j - 1] = temp[j];
                        }
                        else
                        {
                            temp2[j] = temp[j];
                        }
                    }
                    temp = temp2;
                }
            }
            flotilla = temp;
        }
        else
        {
            if (this.getShip(0).getBattleStat(8) == 0)
            {
                flotilla[0].getMoveableObject3d().changeNodeState(false);
                flotilla = new Ship[0];
            }
        }
    }

    public void damageShip(int ship, double damage)
    {
        flotilla[ship].setBattleStat(BattleObject.BATTLE_STAT_HP, damage);
    }

    public void removeShip(int place)
    {
        boolean passed = false;
        Ship[] temp = new Ship[flotilla.length - 1];
        for (int j = 0; j < temp.length; j++)
        {
            if (j == place)
            {
                j++;
                passed = true;
            }
            if (passed)
            {
                temp[j - 1] = flotilla[j];
            }
            else
            {
                temp[j] = flotilla[j];
            }
        }
        flotilla = temp;

        setMovementSpeed();
    }
    static double HPfactor = 10;

    public static void calcSpDamage(Flotilla attack, Flotilla defend, float factor)
    {
        double damage = ((attack.getBattleStat(BattleObject.BATTLE_STAT_SP_POWER) + attack.getBattleStat(BattleObject.BATTLE_STAT_SP_WEAPON_STAT)))
                / (defend.getBattleStat(BattleObject.BATTLE_STAT_SP_DEFENSE) + defend.getBattleStat(BattleObject.BATTLE_STAT_SP_ARMOR_STAT)) * HPfactor * factor;
        //double damage = ((attack.getSpPower() + attack.getSpWeaponStat())) / (defend.getSpDefence() + defend.getSpArmorStat()) * HPfactor;
        defend.setHP(damage);
    }

    public static void calcRegDamage(Flotilla attack, Flotilla defend, float factor)
    {
        double damage = ((attack.getBattleStat(BattleObject.BATTLE_STAT_REG_POWER) + attack.getBattleStat(BattleObject.BATTLE_STAT_REG_WEAPON_STAT)))
                / (defend.getBattleStat(BattleObject.BATTLE_STAT_REG_DEFENSE) + defend.getBattleStat(BattleObject.BATTLE_STAT_REG_ARMOR_STAT)) * HPfactor * factor;
        //double damage = ((attack.getRegPower() + attack.getRegWeaponStat())) / (defend.getRegDefence() + defend.getRegArmorStat()) * HPfactor;
        defend.setHP(damage);
    }

    public static void battle(Flotilla a, Flotilla d, float deltaTime)
    {
        if (a.getFlotilla().length > 0 && d.getFlotilla().length > 0)
        {
            a.setFlotillaStats();
            d.setFlotillaStats();
            calcRegDamage(a, d, deltaTime * BATTLE_TIME_FACTOR);
            calcRegDamage(d, a, deltaTime * BATTLE_TIME_FACTOR);
            calcSpDamage(a, d, deltaTime * BATTLE_TIME_FACTOR);
            calcSpDamage(d, a, deltaTime * BATTLE_TIME_FACTOR);
            a.checkRemoveShip();
            d.checkRemoveShip();
        }
    }
    
    /**
     * Simulates a flotilla battle through a factor of time.
     *
     * @param b The flotilla to battle.
     * @param deltaTime The time between each frame.
     * @return 0 if the battle is not over, -1 if this won, +1 if flotilla b
     * won.
     */
    public int flotillaBattle(Flotilla b, float deltaTime)
    {
        this.setFlotillaStats();
        b.setFlotillaStats();
        
        calcRegDamage(this, b, deltaTime * BATTLE_TIME_FACTOR);
        calcRegDamage(b, this, deltaTime * BATTLE_TIME_FACTOR);
        calcSpDamage(this, b, deltaTime * BATTLE_TIME_FACTOR);
        calcSpDamage(b, this, deltaTime * BATTLE_TIME_FACTOR);


        this.checkRemoveShip();
        b.checkRemoveShip();

        // Probably unecessary, but if they both die at the same time,
        // the defending ship will win.
        if (b.getHP() <= 0)
        {
            b.regenerate();
            return -1;
         
        }
        else
        {
            if (this.getHP() <= 0)
            {
                this.regenerate();
                return 1;
                
            }
            else
            {
                return 0;
            }
        }

    }
    
    /**
     * Resets flotilla positions.
     */
    public void regenerate()
    {
        initialize(flotilla, centerPosition, isFull, name, true);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END BATTLE METHODS /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END UPDATE METHODS /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START GETTERS/SETTERS //////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getName(int place)
    {
        return flotilla[place].getName();
    }

    public Ship[] getFlotilla()
    {
        return flotilla;
    }

    public Vector3f getCenterPosition()
    {
        return centerPosition;
    }

    public BoundingBox getBoundingBox()
    {
        return (BoundingBox) wireBoxGeometry.getWorldBound();
    }

    public Geometry getWireBoxGeometry()
    {
        return wireBoxGeometry;
    }

    public boolean getFull()
    {
        return isFull;
    }

    public void changeFull()
    {
        isFull = !isFull;
    }

    public void setFlotillaStats()
    {
        if (flotilla.length > 0)
        {
            worth = 0;
            crew = 0;
            double m = qualities[BATTLE_STAT_MOVEMENT_SPEED];
            qualities = new double[19];
            qualities[BATTLE_STAT_MOVEMENT_SPEED] = m;
            for (int i = 0; i < 19; i++)
            {
                if (i == BATTLE_STAT_MOVEMENT_SPEED)
                    continue;
                qualities[i] = 0;
            }
            for (int i = 0; i < flotilla.length; i++)
            {
                for (int j = 0; j < 19; j++)
                {
                    if (j == BATTLE_STAT_MOVEMENT_SPEED)
                        continue;
                    qualities[j] = qualities[j] + flotilla[i].getBattleStat(j); // Get jth battle stat
                }
                worth = worth + flotilla[i].getCost();
                crew = crew + flotilla[i].getCrew();
            }
            qualities[4] = qualities[4] / flotilla.length;
            qualities[5] = qualities[5] / flotilla.length;
        }
    }

    public double getFlotillaStats()
    {
        double total = 0;
        for (int i = 0; i < 19; i++)
        {
            total = total + qualities[i];
        }
        return total;
    }

    public double getFlotillaStat(int BATTLE_STAT)
    {
        if (BATTLE_STAT == BATTLE_STAT_MOVEMENT_SPEED)
            return getBattleStat(BATTLE_STAT_MOVEMENT_SPEED);
        
        double total = 0.0;
        for (int i = 0; i < flotilla.length; i++)
        {
            total += flotilla[i].getBattleStat(BATTLE_STAT);
        }

        return total;
    }

    public Ship getShip(int a)
    {
        return flotilla[a];
    }

    public void addShip(Ship one)
    {
        Ship[] temp = new Ship[flotilla.length + 1];
        for (int i = 0; i < flotilla.length; i++)
        {
            temp[i] = flotilla[i];
        }
        temp[flotilla.length] = one;
        flotilla = temp;
    }

    public double getHP()
    {
        double total = 0;
        for (int i = 0; i < flotilla.length; i++)
        {
            total = total + flotilla[i].getBattleStat(BattleObject.BATTLE_STAT_HP);
        }
        return total;
    }

    public void setHP(double damage)
    {
        double temp = damage;
        double shipHP = 0;
        for (int i = 0; i < flotilla.length; i++)
        {
            shipHP = getShip(i).getBattleStat(BattleObject.BATTLE_STAT_HP);
            if (temp > shipHP)
            {
                temp = temp - shipHP;
                getShip(i).setBattleStat(BattleObject.BATTLE_STAT_HP, shipHP);
            }
            else
            {
                getShip(i).setBattleStat(BattleObject.BATTLE_STAT_HP, temp);
                temp = 0;
                break;
            }
        }
    }

    public void setShipsBattleStat(int shipIndex, int BATTLE_STAT, double quality)
    {
        getShip(shipIndex).setBattleStat(BATTLE_STAT, quality);
    }

    @Override
    public void setBattleStat(int BATTLE_STAT, double value)
    {
        super.setBattleStat(BATTLE_STAT, value);
        
        if (BATTLE_STAT == BATTLE_STAT_MOVEMENT_SPEED)
            setMovementSpeed();
    }
    
    public double getBattleStat(int BATTLE_STAT)
    {
        setFlotillaStats();
        return qualities[BATTLE_STAT];
    }

    /**
     * @return true if any ship in the flotilla is transforming, false
     * otherwise.
     */
    public boolean isTransforming()
    {
        boolean isTransforming = false;
        for (int i = 0; i < flotilla.length; i++)
        {
            isTransforming = flotilla[i].isTransforming();
            if (isTransforming)
            {
                break;
            }
        }

        return isTransforming;
    }

    public boolean isMoving()
    {
        boolean isMoving = false;
        for (int i = 0; i < flotilla.length; i++)
        {
            isMoving = flotilla[i].isTransforming();
            if (isMoving)
            {
                break;
            }
        }

        return isMoving;
    }

    public boolean isRotating()
    {
        boolean isRotating = false;
        for (int i = 0; i < flotilla.length; i++)
        {
            isRotating = flotilla[i].isRotating();
            if (isRotating)
            {
                break;
            }
        }

        return isRotating;
    }

    public String[] getDisplayInfo()
    {
        return new String[]
        {
            "Flotilla:",
            " Name: " + name,
            " Battle Stats",
            "  HP: " + (int) getFlotillaStat(BattleObject.BATTLE_STAT_HP),
            "  Reg Power: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_POWER),
            "  Sp Power: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_POWER),
            "  Reg Defense: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_DEFENSE),
            "  Sp Defense: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_DEFENSE),
            "  Reg Attack Cooldown: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ATTACK_COOLDOWN),
            "  Sp Attack Cooldown: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ATTACK_COOLDOWN),
            "  Reg Accuracy: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ACCURACY),
            "  Sp Accuracy: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ACCURACY),
            "  Reg Range: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_RANGE),
            "  Sp Range: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_RANGE),
            "  Reg Armor Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_ARMOR_STAT),
            "  Sp Armor Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_ARMOR_STAT),
            "  Reg Weapon Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_REG_WEAPON_STAT),
            "  Sp Weapon Stat: " + getFlotillaStat(BattleObject.BATTLE_STAT_SP_WEAPON_STAT),
            "  Repair Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_REPAIR_ABILITY),
            "  Transport Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_TRANSPORT_ABILITY),
            "  Build Ability: " + getFlotillaStat(BattleObject.BATTLE_STAT_BUILD_ABILITY),
            "  Movement Speed: " + getBattleStat(BATTLE_STAT_MOVEMENT_SPEED)
        };
    }

    /**
     * Sets the target transformation point for the flotilla.
     *
     * @param targetPoint The point to transform to.
     * @param moveTo Whether or not the flotilla should move to the target point
     * in addition to rotating to it.
     */
    public void setTargetPoint(Vector3f targetPoint, boolean moveTo)
    {
        this.targetPoint = targetPoint;
        Vector3f movementVector = targetPoint.subtract(centerPosition);
        for (int i = 0; i < flotilla.length; i++)
        {
            flotilla[i].setTargetPoint(flotilla[i].getPosition().add(movementVector), moveTo);
        }
    }
    
    @Override
    public Vector3f getPosition()
    {
        return centerPosition;
    }
    
    public DrawableObject3d getDrawableObject3d()
    {
        return model;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END GETTERS/SETTERS ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
}