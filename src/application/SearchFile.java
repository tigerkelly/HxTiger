/*
 * Copyright (c) 2023 Richard Kelly Wiles (rkwiles@twc.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *  Created on: Aug 6, 2023
 *      Author: Kelly Wiles
 */

package application;

import java.util.List;

import org.riversun.bigdoc.bin.BigFileSearcher;
import org.riversun.bigdoc.bin.SearchCondition;

public class SearchFile implements Runnable {
	
	private volatile long position = -1;
	private FileInfo fi = null;
	private long pos = 0;
	private int cores = 1;
	private byte[] pattern;
	private BigFileSearcher searcher = null;
	private SearchChanges sChange = null;
	
	public SearchFile(FileInfo fi, long pos, byte[] pattern) {
		this.fi = fi;
		this.pos = pos;
		this.pattern = pattern;
		
		cores = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void run() {
		searcher = new BigFileSearcher();
		
		searcher.setUseOptimization(true);
	    searcher.setSubBufferSize(265);
	    searcher.setSubThreadSize(cores);
	    
	    final SearchCondition sc = new SearchCondition();
	    
	    sc.srcFile = fi.getFd();
	    sc.startPosition = pos;
	    sc.searchBytes = pattern;
	    
	    List<Long> sf = searcher.searchBigFile(sc);
	    
	    System.out.println(sf);
		
		if (sf != null && sf.size() > 0) {
			if (sf.size() > 1) {
//				System.out.println(sf);
				fi.setPositions(sf);
			}
//			System.out.println("positions = " + sf.get(0));
			position = sf.get(0);
			
			if (sChange != null)
				sChange.fireChange(new SearchChangeEvent(SearchChanges.FOUND, sf));
		} else {
			if (sChange != null)
				sChange.fireChange(new SearchChangeEvent(SearchChanges.NOTFOUND, null));
		}
	}
	
	public long getPosition() {
		return position;
	}
	
	public void stopSearch() {
		searcher.cancel();
	}
	
	public void setChange(SearchChanges sChange) {
		this.sChange = sChange;
	}

}
