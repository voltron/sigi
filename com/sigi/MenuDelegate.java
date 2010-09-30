package com.sigi;

public interface MenuDelegate {

	/**
	 * Delegation method called when menu button is pressed (during onPrepareOptionsMenu)
	 * 
	 * @return	True if native menu should be displayed, false if not
	 */
	public boolean willShowMenu();
	
}
