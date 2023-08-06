/*
 * Created on Mar 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Copyright Kelly Wiles 2005, 2006, 2007, 2008
 */
package application;

import javax.swing.event.EventListenerList;

/**
 * This is a custom event to notify the workbench that a page has been changed.
 * @author Kelly Wiles
 *
 */
public class SearchChanges {
	protected EventListenerList listenerList = new EventListenerList();
	
	public final static int FOUND = 1;
	public final static int NOTFOUND = 2;
	
	// This methods allows classes to register for Changes
    public void addChangeListener(SearchChangeListener listener) {
        listenerList.add(SearchChangeListener.class, listener);
    }

    // This methods allows classes to unregister for Changes
    public void removeChangeListener(SearchChangeListener listener) {
        listenerList.remove(SearchChangeListener.class, listener);
    }

    // This private class is used to fire Changes
    public void fireChange(SearchChangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SearchChangeListener.class) {
                ((SearchChangeListener)listeners[i+1]).changeEventOccurred(evt);
            }
        }
    }
}
