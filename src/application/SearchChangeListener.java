/*
 * Created on Mar 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Copyright Kelly Wiles 2005, 2006, 2007, 2008
 */
package application;

import java.util.EventListener;

/**
 * Part of the custom event Changes class
 * @author Kelly Wiles
 *
 */
public interface SearchChangeListener extends EventListener {
	public abstract void changeEventOccurred(SearchChangeEvent e);
}
