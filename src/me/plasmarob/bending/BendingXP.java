package me.plasmarob.bending;

public enum BendingXP {
	
	AIR ("air"),
	
	EARTH ("earth"),
	SENSE ("sense"),
	METAL ("metal"),
	SAND ("sand"),
	
	
	FIRE ("fire"),
	SMELTING ("smelting"),
	LIGHTNING ("lightning"),
	
	WATER ("water"),
	ICE ("ice"),
	PLANT ("plant"),
	;
	
	
	private final String str;

    BendingXP(String str) {
        this.str = str;
    }
    
    public String toString() {
    	return str;
    }
}
