package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum BendingEarthString {
	
	// new 0.4 architecture
	// old guard
	EARTHDIG ("earthdig"),
	EARTHPILLAR ("earthpillar"),
	EARTHFISSURE ("earthfissure"),
	EARTHTSUNAMI ("earthtsunami"),
	SANDTARGET ("sandtarget"),
	// needed work
	//EARTHGRAB ("earthgrab"),
	EARTHPUSH ("earthpush"),
	EARTHLAUNCH ("earthlaunch")
	;
	
	
    private final String str;

    BendingEarthString(String str) {
        this.str = str;
    }
    
    public static boolean contains(String string)
    {
    	for (BendingEarthString s : BendingEarthString.values())
            if (s.str.equals(string))
            	return true;
    	return false;
    }
    
    //TODO: this lists all moves instead - need to set element qualification
    public static String listEarthMoves()
    {
    	String moves = ChatColor.RED + "Firebending moves:\n";
    	for (BendingEarthString s : BendingEarthString.values())
    	{
    		moves = moves.concat("   - " + s.str + "\n");
    	}
    	return moves;
    }

	public static String help(String form) {
		String helpText = "";
		if (contains(form.toLowerCase()))
		{
			//TODO: add the strings from the GUI to enum list
			form = form.toLowerCase();
		}
		else
		{
			return "err";
		}
		
		return helpText;
	}
    
    /*
    // TODO: move these here from BendingPlayer
    double stuff() {
    	if (move.equals("watergrab"))
    		new WaterGrab(player);
    	if (move.equals("watersplash"))
    		new WaterSplash(player);
    	if (move.equals("watertwister"))
    		new WaterTwister(player);
    	if (move.equals("waterstreaming"))
    		new WaterStreaming(player);
    	if (move.equals("waterwhip"))
    		new WaterWhip(player);
    }
    */
}