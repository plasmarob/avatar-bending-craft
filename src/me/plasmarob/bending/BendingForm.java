package me.plasmarob.bending;

public interface BendingForm {
	public String codeName();
	public String showName();
	public int guiPosition();
	public int level();
	
	public static BendingForm getBySlot(int num) { return null; }
	public static String listMoves() { return null; }
}
