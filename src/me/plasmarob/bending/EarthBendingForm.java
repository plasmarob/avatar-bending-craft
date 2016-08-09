package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum EarthBendingForm implements BendingForm {

	EARTHDIG ("earthdig","Dig", 10,15),
	EARTHPILLAR ("earthpillar","Pillar", 11,10),
	EARTHFISSURE ("earthfissure","Fissure", 12,40),
	EARTHTSUNAMI ("earthtsunami","Tsunami", 13,45),
	SANDTARGET ("sandtarget","Trap", 14,25),
	// needed work
	//EARTHGRAB ("earthgrab"),
	EARTHPUSH ("earthpush","Push", 15,1),
	EARTHLAUNCH ("earthlaunch","Launch", 16,5),
	
	EARTHSENSE ("earthsense","Sense",22, 30)
	;
	
	private final String codeName;
	private final String showName;
    private final int guiPos;
    private final int level;
    
	EarthBendingForm(String codeName, String showName, int guiPos, int level) {
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
		for (EarthBendingForm bf : EarthBendingForm.values())
            if (bf.guiPos == num)
            	return bf;
    	return null;
	}
	
	public static String listMoves()
    {
    	String moves = ChatColor.WHITE + "Airbending moves:\n";
    	for (EarthBendingForm bf : EarthBendingForm.values())
    	{
    		moves = moves.concat("   - " + bf.showName + "\n");
    	}
    	return moves;
    }
	
}
