package me.plasmarob.bending;

public enum PlayerAction {
	
	
	SNEAK_OFF (0),
	SNEAK_ON (1),
	LEFT_CLICK (2),
	RIGHT_CLICK (3);
	
    private final int id;
    PlayerAction(int id) {
        this.id = id;
    }
    
    public int val()
    {
    	return id;
    }
    
   
}
