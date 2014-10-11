package AI;

import java.util.ArrayList;
		


public class Player 
{
	//viewable to others
	public Technology currentTech;
	public Reputation rep; // Ai only
	public ArrayList<Player> allies;
	public boolean atWar;
	
	
	//not viewable to others
	private double money;
	private ArrayList<Technology> researched;
	private ArrayList<Trade> trades;
	private int playerNumber;
	
	//Fog of War (light)
	//explored terrain needs a way to be kept track of
	private Map map;
	private ArrayList<Unit> units;
	
	public int level;
	
	
}
