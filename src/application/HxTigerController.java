package application;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HxTigerController implements Initializable {
	
	private HxGlobal hg = HxGlobal.getInstance();
	private FileChooser fc = null;
	private SearchFile searchFile = null;
	private SearchChanges searchChange = null;

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnAbout;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblVersion;
    
    @FXML
    private MenuItem mFileOpen;

    @FXML
    private MenuItem mFileQuit;

    @FXML
    private TabPane tabPane;
    
    @FXML
    private ToggleGroup blocktoogle;
    
    @FXML
    private RadioMenuItem rmBlock16;

    @FXML
    private RadioMenuItem rmBlock32;
    
    @FXML
    void doBlock16(ActionEvent event) {
    	hg.blockSize = 16;
    }

    @FXML
    void doBlock32(ActionEvent event) {
    	hg.blockSize = 32;
    }

    @FXML
    void doBtnAbout(ActionEvent event) {
    	hg.centerScene(aPane, "About.fxml", "About HxTiger", null);
    }
    
    @FXML
    void doFileOpen(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	fc.getExtensionFilters().addAll(
		     new FileChooser.ExtensionFilter("All Files", "*.*")
		);
    	File fd = fc.showOpenDialog(stage);
		if (fd != null) {
			FileInfo fi = createTab(fd);
			
			hg.files.put(fd.getAbsolutePath(), fi);
		}
    }

    @FXML
    void doFileQuit(ActionEvent event) {
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doHelpAbout(ActionEvent event) {
    	hg.centerScene(aPane, "About.fxml", "About HxTiger", null);
    }
    
    @FXML
    void doHelpHelp(ActionEvent event) {
    	hg.centerScene(aPane, "Help.fxml", "HxTiger Help", null);
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fc = new FileChooser();
		
		searchChange = new SearchChanges();
		
		searchChange.addChangeListener(new SearchChangeListener() {

			@Override
			public void changeEventOccurred(SearchChangeEvent e) {
				final List<Long> data = e.data;
				
				Tab tab = tabPane.getSelectionModel().getSelectedItem();
				if (tab == null)
					return;
				FileInfo fi = (FileInfo)tab.getUserData();
				
				if (e.type == SearchChanges.FOUND) {
					
					fi.setPositions(data);
					
					fi.getChoice().getItems().clear();
					
					if (data != null) {
						for (long v : data)
							fi.getChoice().getItems().add(v);
					}
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							fi.getSlider().setValue(data.get(0) / 16);
							fi.getChoice().setValue(data.get(0));
							fi.getSearchStatus().setText(String.format("Found %d matches.", data.size()));
						}
					});
				} else if (e.type == SearchChanges.NOTFOUND) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							fi.getSearchStatus().setText("No matches found.");
						}
					});
				}
				
				aPane.setCursor(Cursor.DEFAULT);
			}
			
		});
	}

	private FileInfo createTab(File fd) {
		FileInfo fi = new FileInfo(fd);
		fi.setBlockSize(hg.blockSize);
		fi.setLastBlock(-1);
		Tab tab = new Tab(fd.getName());
		tab.setUserData(fi);
		tab.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 10;");
		
		tabPane.getTabs().add(tab);
		
		AnchorPane ap = new AnchorPane();
		tab.setContent(ap);
		
		VBox vb = new VBox();
		
		AnchorPane.setBottomAnchor(vb, 2.0);
		AnchorPane.setLeftAnchor(vb, 4.0);
		AnchorPane.setRightAnchor(vb, 4.0);
		AnchorPane.setTopAnchor(vb, 2.0);
		
		ListView<String> lv = new ListView<String>();
		lv.setStyle("-fx-font-size: 18px; -fx-font-family: Monospaced;");
		
		ContextMenu cm = new ContextMenu();
		
		MenuItem edit = new MenuItem ("Edit block");
		edit.setStyle("-fx-font-size: 16px; -fx-font-family: SanSerif;");
		
		edit.setOnAction((e) -> {
			FXMLLoader loader = hg.loadScene(aPane, "Edit.fxml", "Edit Block", null);
	    	EditController ec = (EditController)loader.getController();
	    	
	    	ec.setSize(fi.getBlockSize());
	    	String line = lv.getSelectionModel().getSelectedItem();
	    	if (line.isBlank() == false)
	    		ec.setLine(line);
	    	
	    	Stage stage = (Stage)ec.getStage();
	    	
	    	stage.showAndWait();
	    	
	    	if (hg.editSaved == true) {
	    		hg.editSaved = false;
	    		
	    		String[] arr = line.split("\\s+");
	    		long offset = Long.parseLong(arr[0], 16);
	    		
	    		String name = fi.getFd().getName();
	    		String newName = null;
	    		int x = name.lastIndexOf('.');
	    		if (x == -1) {
	    			newName = name + "_bkp";
	    		} else {
	    			newName = name.substring(0, x) + "_bkp" + name.substring(x);
	    		}
	    		
    			File f = new File(fi.getFd().getParent() + File.separator + newName);
	    		if (f.exists() == false) {
	    			FXMLLoader l = hg.loadScene(aPane, "Backup.fxml", "Backup File", null);
	    	    	BackupController bc = (BackupController)l.getController();
	    	    	
	    	    	bc.setMessage("Backup the file\n" + fi.getFd().getAbsolutePath() +"\nBefore making changes?");
	    	    	
	    	    	Stage stage2 = (Stage)bc.getStage();
	    	    	
	    	    	stage2.showAndWait();
	    	    	
	    	    	if (hg.backupFile == true) {
	    	    		fi.setBackup(true);
	    	    		FXMLLoader l2 = hg.loadScene(aPane, "CopyProgress.fxml", "Copy File", null);
	    	    		CopyProgressController cpc = (CopyProgressController)l2.getController();
	    	    	
	    	    		Stage stage3 = (Stage)cpc.getStage();
	    	    		cpc.setFileInOut(fi.getFd(), f);
	    	    		
	    	    		stage3.showAndWait();
	    	    	}
	    		}
	    		
	    		try {
					fi.getRaf().seek(offset);
					
					fi.getRaf().write(hg.editBytes);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	    		
	    		String hexStr = buildLine(fi, offset, hg.editBytes);
	    		
	    		String offsetStr = String.format("%08x", offset);
	    		
	    		int idx = -1;
	    		for (String s : lv.getItems()) {
	    			idx++;
	    			if (s.startsWith(offsetStr) == true) {
	    				break;
	    			}
	    		}
	    		
	    		if (idx >= 0)
	    			lv.getItems().set(idx,  hexStr);
	    	}
		});
		
		cm.getItems().add(edit);
		
		lv.setContextMenu(cm);
		
		HBox hb = new HBox();
		hb.setPadding(new Insets(4));
		hb.setSpacing(4.0);
		hb.setAlignment(Pos.CENTER_LEFT);
		
		Label lblZero = new Label("0");
		Label lblMax = new Label(fi.getBlockNum() + "");
		
		Slider slider = new Slider(0, fi.getBlockNum(), 0);
		slider.setMinorTickCount(2);
		slider.setBlockIncrement(1);
		
		fi.setSlider(slider);
		
		TextField tfValue = new TextField("0");
		tfValue.setPrefWidth(75.0);
		tfValue.setStyle("-fx-font-size: 14px;");
		
		Tooltip ttV = new Tooltip("");
		ttV.setStyle("-fx-font-size: 16px;");
		
		ttV.setOnShowing((e) -> {
			String s = "Current block offset being displayed.";
			int b = Integer.parseInt(tfValue.getText());
			s += String.format("\nByte offset %d (0x%x)", (b * 16), (b * 16));
			
			ttV.setText(s);
		});
		
		tfValue.setTooltip(ttV);
		
		fi.setTfValue(tfValue);
		
		Tooltip tt = new Tooltip("Number of " + fi.getBlockSize() + " byte blocks in file.");
		tt.setStyle("-fx-font-size: 16px;");
		
		lblMax.setTooltip(tt);
		
		slider.setOnMouseReleased((e) -> {
			tfValue.setText((int)slider.getValue() + "");
			loadFile(lv, (int)slider.getValue());
		});
		
		tfValue.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				String v = tfValue.getText();
				if (v.isBlank() == false) {
					slider.setValue(Integer.parseInt(v));
					loadFile(lv, Integer.parseInt(v));
				}
			}
		});
		
		tfValue.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (tfValue.isFocused() && !tfValue.getText().isEmpty()) {
							tfValue.selectAll();
						}
					}
				});
			}

		});
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				tfValue.setText(arg2.intValue() + "");
				loadFile(lv, arg2.intValue());
			}
			
		});
		
		HBox.setHgrow(slider, Priority.ALWAYS);
		
		hb.getChildren().addAll(lblZero, slider, lblMax, tfValue);
		
		VBox.setVgrow(lv, Priority.ALWAYS);
		
		HBox hb2 = new HBox();
		hb2.setPadding(new Insets(4));
		hb2.setSpacing(4.0);
		hb2.setAlignment(Pos.CENTER_LEFT);
		
//		Label lbl = new Label("Search:");
//		lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: SanSerif;");
		
		TextField tf = new TextField();
		tf.setPrefWidth(200.0);
		tf.setStyle("-fx-font-size: 15px;");
		
		Tooltip ttTf = new Tooltip("Enter 2 digit hex values separated by a space.\nExample: dd a2 00");
		ttTf.setStyle("-fx-font-size: 16px;");
		
		tf.setTooltip(ttTf);
		
		Button btnSearch = new Button("Search");
		btnSearch.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
		
		ComboBox<Long> cb = new ComboBox<Long>();
		cb.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
		cb.setPrefWidth(125.0);
		
		Tooltip cbTt = new Tooltip("Offset in bytes of found pattern matches.");
		cbTt.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
		
		cb.setTooltip(cbTt);
		
		cb.setOnAction((e) -> {
			if (cb.getValue() == null)
				return;
			
			long pos = cb.getValue();
			System.out.println(pos);
			fi.getSlider().setValue(pos / 16);
		});
		
		fi.setChoice(cb);
		
		btnSearch.setOnAction((e) -> {
			String v = tf.getText();
			if (v.isBlank() == true)
				return;
			
			aPane.setCursor(Cursor.WAIT);
			cb.getItems().clear();
			String[] a = v.split("\\s+");
			byte[] b = new byte[a.length];
			for (int i = 0; i < a.length; i++) {
				b[i] = (byte)(Integer.parseInt(a[i], 16) & 0xff);
			}
			
			FileInfo f = (FileInfo)tab.getUserData();
			fi.setPositions(null);
			fi.setLastPosition(0);
			
			if (searchFile != null)
				searchFile.stopSearch();
			
			searchFile = new SearchFile(f, 0, b);
			searchFile.setChange(searchChange);
			
			Thread th = new Thread(searchFile);
			th.setDaemon(true);
			th.start();
		});
		
//		Button btnNext = new Button("Next");
//		btnNext.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
//		
//		btnNext.setOnAction((e) -> {
//			String v = tf.getText();
//			fi.setLastPattern(v);
//			String[] a = v.split("\\s+");
//			byte[] b = new byte[a.length];
//			for (int i = 0; i < a.length; i++) {
//				b[i] = (byte)(Integer.parseInt(a[i], 16) & 0xff);
//			}
//			
//			FileInfo f = (FileInfo)tab.getUserData();
//			if (f.getPositions() == null)
//				return;
//			
//			List<Long> data = f.getPositions();
//			int idx = (int)fi.getLastPosition();
//			idx++;
//			fi.setLastPosition(idx);
//			if (idx >= data.size())
//				return;
//			
//			long pos = (data.get(idx) / 16);
//			
////			System.out.println(data.get(idx) + " / 16 = " + (data.get(idx) / 16));
//			fi.getSlider().setValue(data.get(idx) / 16);
//			
//			loadFile(lv, pos);
//		});
		
		Button btnStopSearch = new Button("Stop");
		btnStopSearch.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
		
		btnStopSearch.setOnAction((e) -> {
			if (searchFile != null) {
				searchFile.stopSearch();
				searchFile = null;
				aPane.setCursor(Cursor.DEFAULT);
			}
		});
		
		Tooltip ttStop = new Tooltip("Stop an active search.");
		ttStop.setStyle("-fx-font-size: 15px; -fx-font-family: SanSerif;");
		
		btnStopSearch.setTooltip(ttStop);
		
		Label lblSearchStatus = new Label();
		lblSearchStatus.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: SanSerif; -fx-text-fill: blue;");
		
		fi.setSearchStatus(lblSearchStatus);
		
		hb2.getChildren().addAll(tf, btnSearch, cb, btnStopSearch, lblSearchStatus);
		
		vb.getChildren().addAll(hb, lv, hb2);
		
		ap.getChildren().add(vb);
		
		loadFile(lv, 0);
		
		return fi;
	}
	
//	private void searchFile(ListView<String> lv, long pos, byte[] pattern) {
//		
//		Tab tab = tabPane.getSelectionModel().getSelectedItem();
//		if (tab == null)
//			return;
//		
//		FileInfo fi = (FileInfo)tab.getUserData();
//		
//		fi.getSearchStatus().setText("");
//		
//		if (searcher != null) {
//			searcher.cancel();
//			searcher = null;
//		}
//		
//		searcher = new BigFileSearcher();
//		
//		searcher.setUseOptimization(true);
//	    searcher.setSubBufferSize(265);
//	    searcher.setSubThreadSize(cores);
//	    
//	    final SearchCondition sc = new SearchCondition();
//	    
//	    sc.srcFile = fi.getFd();
//	    if (pos == -1)
//	    	sc.startPosition = foundPos + 1;
//	    else
//	    	sc.startPosition = pos;
//	    sc.searchBytes = pattern;
//	    
//		sc.onRealtimeResultListener = new OnRealtimeResultListener() {
//
//			@Override
//			public void onRealtimeResultListener(float progress, List<Long> pointerList) {
////				System.out.println("progress:" + progress + " pointerList:" + pointerList);
//			}
//		};
//
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
//
//		th.setDaemon(true);
//		th.start();
//		
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
//
//	}
	
	private void loadFile(ListView<String> lv, long pos) {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		if (tab == null)
			return;
		
		FileInfo fi = (FileInfo)tab.getUserData();
		
		if (fi.getLastBlock() == pos)
			return;
		
		lv.getItems().clear();
		
		fi.setLastBlock(pos);
		
		long skipNum = pos * fi.getBlockSize();
		if (skipNum > 0 && (skipNum % fi.getBlockSize()) > 0)
			skipNum--;
		
		byte[] b = new byte[fi.getBlockSize()];
		RandomAccessFile raf = fi.getRaf();
		try {
			raf.seek(skipNum);
			
			int count = 0;
			
			while(raf.read(b) != -1) {
				String s = buildLine(fi, (pos * fi.getBlockSize()), b);
				
				lv.getItems().add(s);
				
				count++;
				if (count > 200)
					break;
				
				pos++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String buildLine(FileInfo fi, long offset, byte[] b) {
		String str = null;
			
		int n = b.length;
		str = String.format("%08x ", offset);
		
		for (int i = 0; i < n; i++) {
			if (i == (fi.getBlockSize() / 2) - 1)
				str += String.format("%02x - ", b[i]);
			else
				str += String.format("%02x ", b[i]);
		}
		
		str += " ";
		
		if (n < fi.getBlockSize()) {
			for (int i = n; i < fi.getBlockSize(); i++)
				str += "   ";
		}
		
		for (int i = 0; i < n; i++) {
			if (Character.isAlphabetic(b[i]) == true) {
				str += (char)b[i];
			} else {
				str += ".";
			}
		}
		
		return str;
	}
}
