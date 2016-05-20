package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum BendingAirString {
	
	// new 0.4 architecture

	// old guard
	AIRGUST ("airgust"),
	AIRBLADE ("airblade"),
	AIRSCOOTER ("airscooter"),
	AIRSHIELD ("airshield"),
	AIRTWISTER ("airtwister");
	
    private final String str;

    BendingAirString(String str) {
        this.str = str;
    }
    
    public static boolean contains(String string)
    {
    	for (BendingAirString s : BendingAirString.values())
            if (s.str.equals(string))
            	return true;
    	return false;
    }
    
    //TODO: this lists all moves instead - need to set element qualification
    public static String listAirMoves()
    {
    	String moves = ChatColor.RED + "Firebending moves:\n";
    	for (BendingAirString s : BendingAirString.values())
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