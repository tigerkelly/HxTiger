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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HxGlobal {

	private static HxGlobal singleton = null;
	
	private HxGlobal() {
		initGlobals();
	}
	
	private void initGlobals() {
		appVersion = "1.0.0";
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win") == true) {
			osType = 1;
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			osType = 2;
		} else if (os.contains("mac") == true) {
			osType = 3;
		}
		
		files = new HashMap<String, FileInfo>();
		
		sceneNav = new SceneNav();
		
		blockSize = 16;
	}
	
	public String appVersion = null;
	
	public Map<String, FileInfo> files = null;
	
	public int osType = 0;
	public boolean editSaved = false;
	public boolean backupFile = false;
	public byte[] editBytes = null;
	
	public int blockSize = 0;
	
	Alert alert = null;
	
	public SceneNav sceneNav = null;
	
	public static HxGlobal getInstance() {
		// return SingletonHolder.singleton;
		if (singleton == null) {
			synchronized (HxGlobal.class) {
				singleton = new HxGlobal();
			}
		}
		return singleton;
	}
	
	public String scenePeek() {
		if (sceneNav.sceneQue == null || sceneNav.sceneQue.isEmpty())
			return SceneNav.HXTIGER;
		else
			return sceneNav.sceneQue.peek();
	}

	public void guiRestart(String msg) {
		String errMsg = String.format("A GUI error occurred.\r\nError loading %s\r\n\r\nRestarting GUI.", msg);
		showAlert("GUI Error", errMsg, AlertType.CONFIRMATION, false);
		System.exit(1);
	}

	public void loadSceneNav(String fxml) {
		if (sceneNav.loadScene(fxml) == true) {
			guiRestart(fxml);
		}
	}
	
//	public void loadVendors() {
//		
//		try {
//			List<String> allLines = Files.readAllLines(Paths.get(vendorFile.getAbsolutePath()));
//
//			for (String line : allLines) {
//				line = line.trim();
//				if (line.length() <= 0)
//					continue;
//				if (line.charAt(0) == '#')
//					continue;
//				
//				String[] arr = line.split(",");
//				if (arr.length >= 2)
//					vendors.put(arr[0].toLowerCase(), arr[1]);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
////		for (String key : macs.keySet()) {
////			System.out.println(key + ", " + macs.get(key));
////		}
//	}
//	
//	public void loadMacs() {
//		
//		try {
//			List<String> allLines = Files.readAllLines(Paths.get(macFile.getAbsolutePath()));
//
//			for (String line : allLines) {
//				line = line.trim();
//				if (line.length() <= 0)
//					continue;
//				if (line.charAt(0) == '#')
//					continue;
//				
//				String[] arr = line.split(",");
//				if (arr.length >= 2)
//					macs.put(arr[0].toLowerCase(), arr[1]);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
////		for (String key : macs.keySet()) {
////			System.out.println(key + ", " + macs.get(key));
////		}
//	}
	
	public void closeAlert() {
		if (alert != null) {
			alert.close();
			alert = null;
		}
	}
	
	public ButtonType yesNoAlert(String title, String msg, AlertType alertType) {
		ButtonType yes = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType no = new ButtonType("No", ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(alertType, msg, yes, no);
		alert.getDialogPane().setPrefWidth(500.0);
		alert.setTitle(title);
		alert.setHeaderText(null);
		
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get();
	}

	public ButtonType showAlert(String title, String msg, AlertType alertType, boolean yesNo) {
		alert = new Alert(alertType);
		alert.getDialogPane().setPrefWidth(500.0);
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			if (yesNo == true) {
				if (button.getText().equals("Cancel"))
					button.setText("No");
				else if (button.getText().equals("OK"))
					button.setText("Yes");
			}
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);

		alert.setContentText(msg);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		ButtonType bt = alert.showAndWait().get();

		alert = null;

		return bt;
	}
	
	public void showOutput(String title, String msg, AlertType alertType, boolean yesNo) {
		alert = new Alert(alertType);
		alert.getDialogPane().setPrefWidth(750.0);
		alert.getDialogPane().setPrefHeight(450.0);
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			if (yesNo == true) {
				if (button.getText().equals("Cancel"))
					button.setText("No");
				else if (button.getText().equals("OK"))
					button.setText("Yes");
			}
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);
	
		TextArea txt = new TextArea(msg);
		txt.setStyle("-fx-font-size: 16px;");
		txt.setWrapText(true);

		alert.getDialogPane().setContent(txt);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		alert.showAndWait().get();

		alert = null;
	}

	public void Msg(String msg) {
		System.out.println(msg);
	}
	
	public boolean copyFile(File in, File out) {
		
		try {
	        FileInputStream fis  = new FileInputStream(in);
	        FileOutputStream fos = new FileOutputStream(out);
	        byte[] buf = new byte[8192];
	        int i = 0;
	        while((i=fis.read(buf))!=-1) {
	            fos.write(buf, 0, i);
	        }
	        fis.close();
	        fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
    }
	
	public void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	public boolean isDigit(String s) {
		boolean tf = true;
		
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i)) == false) {
				tf = false;
				break;
			}
		}
		
		return tf;
	}
	
	public void centerScene(Node node, String fxml, String title, String data) {
		FXMLLoader loader = null;
		try {
			Stage stage = new Stage();
			stage.setTitle(title);

			loader = new FXMLLoader(getClass().getResource(fxml));

			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setScene(new Scene(loader.load()));
			stage.hide();

			Stage ps = (Stage) node.getScene().getWindow();

			ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
				double stageWidth = newValue.doubleValue();
				stage.setX(ps.getX() + ps.getWidth() / 2 - stageWidth / 2);
			};
			ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
				double stageHeight = newValue.doubleValue();
				stage.setY(ps.getY() + ps.getHeight() / 2 - stageHeight / 2);
			};

			stage.widthProperty().addListener(widthListener);
			stage.heightProperty().addListener(heightListener);

			// Once the window is visible, remove the listeners
			stage.setOnShown(e2 -> {
				stage.widthProperty().removeListener(widthListener);
				stage.heightProperty().removeListener(heightListener);
			});

			stage.showAndWait();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public FXMLLoader loadScene(Node node, String fxml, String title, String data) {
		FXMLLoader loader = null;
		try {
			Stage stage = new Stage();
			stage.setTitle(title);

			loader = new FXMLLoader(getClass().getResource(fxml));

			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setScene(new Scene(loader.load()));
			stage.hide();

			Stage ps = (Stage) node.getScene().getWindow();

			ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
				double stageWidth = newValue.doubleValue();
				stage.setX(ps.getX() + ps.getWidth() / 2 - stageWidth / 2);
			};
			ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
				double stageHeight = newValue.doubleValue();
				stage.setY(ps.getY() + ps.getHeight() / 2 - stageHeight / 2);
			};

			stage.widthProperty().addListener(widthListener);
			stage.heightProperty().addListener(heightListener);

			// Once the window is visible, remove the listeners
			stage.setOnShown(e2 -> {
				stage.widthProperty().removeListener(widthListener);
				stage.heightProperty().removeListener(heightListener);
			});

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return loader;
	}
	
	public ProcessRet runProcess(String[] args, Object obj) {
    	Process p = null;
    	ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(true);
		pb.command(args);

		try {
			p = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		@SuppressWarnings("resource")
		StreamGobbler inGobbler = new StreamGobbler(p.getInputStream(), obj);
		inGobbler.start();

		int ev = 0;
		try {
			ev = p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return new ProcessRet(ev, inGobbler.getOutput());
    }
}
