package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum WaterBendingForm implements BendingForm {

	WATERBLOB ("waterblob", "Blob", 10,1),
	WATERSPLASH ("watersplash", "Splash", 11,5),
	ICECRAWLER ("icecrawler", "Ice Crawler", 12, 30),
	WATEREXTINGUISH ("waterextinguish", "Heal", 13,10),
	WATERWHIP ("waterwhip", "Whip", 14,32),
	ICECHANGE ("icechange", "Freeze", 15,15),
	// 16 is broken
	
	ICESHIELD ("iceshield", "Shield", 19, 25),
	WATERBUBBLE ("waterbubble", "Bubble", 20,42),
	WATERTSUNAMI ("watertsunami", "Tsunami", 21, 20),
	WATERBLADE ("waterblade", "Blade", 22,40),
	WATERBLAST ("waterblast", "Blast", 23,12),
	// 24 is broken
	WATERTWISTER ("watertwister", "Twister", 25,45)
	
	// BROKE WATERSTREAMING ("waterstreaming", "Stream", 0,27),
	//ICEGROWTH ("icegrowth", "Growth", 0,22),
	
	;
	
	private final String codeName;
	private final String showName;
    private final int guiPos;
    private final int level;
    
	WaterBendingForm(String codeName, String showName, int guiPos, int level) {
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
		for (WaterBendingForm bf : WaterBendingForm.values())
            if (bf.guiPos == num)
            	return bf;
    	return null;
	}
	
	public static String listMoves()
    {
    	String moves = ChatColor.WHITE + "Airbending moves:\n";
    	for (WaterBendingForm bf : WaterBendingForm.values())
    	{
    		moves = moves.concat("   - " + bf.showName + "\n");
    	}
    	return moves;
    }
	
}
