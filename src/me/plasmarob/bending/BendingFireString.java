package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum BendingFireString {
	
	// new 0.4 architecture
	FIREEMIT ("fireemit"),
	FIREWAVE ("firewave"),
	FIREBEAM ("firebeam"),
	FIRELINE ("fireline"),
	FIREWALL ("firewall"),
	LIGHTNING ("lightning"),
	FIRELAUNCH ("firelaunch"),
	COMBUSTION ("combustion"),
	FIREHEAT ("fireheat"),
	FIREBLADE ("fireblade"),
	FIRECOOL ("firecool"),
	FIREBLAST ("fireblast"),
	// old guard
	FIREBALL ("fireball"),
	FIREJET ("firejet"),
	FIRESHIELD ("fireshield"),
	FIRESPINKICK ("firespinkick");
	
	
    private final String str;

    BendingFireString(String str) {
        this.str = str;
    }
    
    public static boolean contains(String string)
    {
    	for (BendingFireString s : BendingFireString.values())
            if (s.str.equals(string))
            	return true;
    	return false;
    }
    
    //TODO: this lists all moves instead - need to set element qualification
    public static String listFireMoves()
    {
    	String moves = ChatColor.RED + "Firebending moves:\n";
    	for (BendingFireString s : BendingFireString.values())
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
			if (form.equals(FIREEMIT.str))
				helpText = 
				"Emit :\n" +
				"   Perform basic firebending kicks and punches.\n" +
				"L-click to punch, and tap sneak to kick."
				;
			else if (form.equals(FIREWAVE.str))
				helpText = 
				"Wave :\n" +
				"   Sling a wave of fire outward.\n" +
				"L-click to punch, and tap sneak to kick."
				;
			
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