package application;

import java.util.List;

import org.riversun.bigdoc.bin.BigFileSearcher;
import org.riversun.bigdoc.bin.SearchCondition;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SearchTaskx extends Task<Long> {
	
	private TabPane tabPane;
	private long pos = 0;
	private byte[] pattern;
	private BigFileSearcher searcher = null;
	private int cores = 1;
	private long foundPos = -1;
	private FileInfo fi = null;
	
	public SearchTaskx(TabPane tabPane, long pos, byte[] pattern) {
		this.tabPane = tabPane;
		this.pos = pos;
		this.pattern = pattern;
		
		cores = Runtime.getRuntime().availableProcessors();
	}

	@Override
	protected Long call() throws Exception {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		if (tab == null)
			return null;
		
		fi = (FileInfo)tab.getUserData();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fi.getSearchStatus().setText("");
			}
		});
		
		if (searcher != null) {
			searcher.cancel();
			searcher = null;
		}
		
		searcher = new BigFileSearcher();
		
		searcher.setUseOptimization(true);
	    searcher.setSubBufferSize(265);
	    searcher.setSubThreadSize(cores);
	    
	    final SearchCondition sc = new SearchCondition();
	    
	    sc.srcFile = fi.getFd();
	    if (pos == -1)
	    	sc.startPosition = foundPos + 1;
	    else
	    	sc.startPosition = pos;
	    sc.searchBytes = pattern;
	    
//		sc.onRealtimeResultListener = new OnRealtimeResultListener() {
//			@Override
//			public void onRealtimeResultListener(float progress, List<Long> pointerList) {
//				System.out.println("progress:" + progress + " pointerList:" + pointerList);
//			}
//		};
		
		List<Long> sf = searcher.searchBigFile(sc);
		
		if (sf != null && sf.size() > 0) {
			if (sf.size() > 1) {
//				System.out.println(sf);
				fi.setPositions(sf);
			} else {
//					System.out.println("positions = " + sf.get(0));
				foundPos = sf.get(0);
			}
		} else {
			foundPos = -1;
		}
		
		if (foundPos != -1) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fi.getSlider().setValue(foundPos / 16);
				}
			});
		}
//		updateValue(foundPos);

//		final Thread th = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				List<Long> sf = searcher.searchBigFile(sc);
//				
//				if (sf != null && sf.size() > 0) {
//					if (sf.size() > 1) {
////						System.out.println(sf);
//						fi.setPositions(sf);
//					} else {
//	//					System.out.println("positions = " + sf.get(0));
//						foundPos = sf.get(0);
//					}
//				} else {
//					foundPos = -1;
//				}
//			}
//		});
		
//		while (th.isAlive() == true) {
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		if (foundPos != -1) {
//			fi.getSlider().setValue((foundPos / fi.getBlockSize()));
//			long seg = foundPos / fi.getBlockSize();
//			loadFile(lv, seg);
//		} else {
//			fi.getSearchStatus().setText("Reached end of file or pattern not found.");
//		}
		
		return null;
	}
	
	public void stopSearch() {
		searcher.cancel();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fi.getSearchStatus().setText("Search canceled.");
			}
		});
	}

}
