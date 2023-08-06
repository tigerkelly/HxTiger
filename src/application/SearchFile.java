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
