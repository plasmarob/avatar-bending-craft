package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum FireBendingForm implements BendingForm {

	FIREHEAT ("fireheat","Heat",10,2),	//broken - no cook pork
	FIREEMIT ("fireemit","Emit",11,10), 
	FIREBEAM ("firebeam","Beam",12,15),
	FIRECOOL ("firecool","Cool",13,5),
	FIRESPINKICK ("firespinkick","Spin Kick",14,20),
	FIREWAVE ("firewave","Wave",15,12),
	FIREJET ("firejet","Jet", 16,40),
	
	FIRELINE ("fireline","Toss", 19,25), //Toss
	FIREBALL ("fireball","Fireball", 20,30),
	FIREWALL ("firewall","Wall", 21,45),
	FIREBLADE ("fireblade","Blade", 22,47),
	FIRELAUNCH ("firelaunch","Launch", 23,40),
	FIRESHIELD ("fireshield","Shield", 24,50),
	FIREBLAST ("fireblast","Blast", 25,48),
	COMBUSTION ("combustion","Combustion", 30,50),
	LIGHTNING ("lightning","Lightning", 32,50)
	;
	
	private final String codeName;
	private final String showName;
    private final int guiPos;
    private final int level;
    
	FireBendingForm(String codeName, String showName, int guiPos, int level) {
		this.codeName = codeName;
		this.showName = showName;
		this.guiPos = guiPos;
		this.level = level;
	}
	
	public String codeName() { return codeName; }
	public String showName() { return showName; }
	public int guiPosition() { return guiPos; }
	public int level() { return level; }
	

	public static BendingForm getBySlot(int num) {
		for (FireBendingForm bf : FireBendingForm.values())
            if (bf.guiPos == num)
            	return bf;
    	return null;
	}
	
	public static String listMoves()
    {
    	String moves = ChatColor.WHITE + "Airbending moves:\n";
    	for (FireBendingForm bf : FireBendingForm.values())
    	{
    		moves = moves.concat("   - " + bf.showName + "\n");
    	}
    	return moves;
    }
	
	
	
	public static String help(String form) {
		String helpText = "";
		/*
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
		*/
		return helpText;
		
	}
}
