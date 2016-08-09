package me.plasmarob.bending;

import org.bukkit.ChatColor;

public enum AirBendingForm implements BendingForm {

	AIR_GUST ("airgust","Gust", 12,1),
	AIR_BLADE ("airblade","Blade", 11,10),
	AIR_SCOOTER ("airscooter","Scooter", 13,20),
	AIR_TWISTER ("airtwister","Twister", 15,25),
	AIR_SHIELD ("airshield","Shield", 14,30),
	SPIRITUAL_PROJECTION ("spiritualprojection","Projection", 22,35)
	;
	
	private final String codeName;
	private final String showName;
    private final int guiPos;
    private final int level;
    
	AirBendingForm(String codeName, String showName, int guiPos, int level) {
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
		for (AirBendingForm bf : AirBendingForm.values())
            if (bf.guiPos == num)
            	return bf;
    	return null;
	}
	
	public static String listMoves()
    {
    	String moves = ChatColor.WHITE + "Airbending moves:\n";
    	for (AirBendingForm bf : AirBendingForm.values())
    	{
    		moves = moves.concat("   - " + bf.showName + "\n");
    	}
    	return moves;
    }
	
}
