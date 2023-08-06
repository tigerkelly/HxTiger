/*
 * Created on Mar 19, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 * 
 * Copyright Kelly Wiles 2005, 2006, 2007, 2008
 */
package application;

import java.util.EventObject;
import java.util.List;

/**
 * Part of the custom event Changes class.
 * @author Kelly Wiles
 *
 */
public class SearchChangeEvent extends EventObject {
	public int type = 0;
	public List<Long> data = null;

	public SearchChangeEvent(Object arg0, List<Long> data) {
		super(arg0);
		type = (Integer)arg0;
		this.data = data;
	}

	private static final long serialVersionUID = 1L;

}
