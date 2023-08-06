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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class EditController implements Initializable {
	
	private HxGlobal hg = HxGlobal.getInstance();
	private String line = null;
	private int blockSize = 0;

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private HBox hbox;

    @FXML
    private Label lblTitle;
    
    @FXML
    private Label lblBlock;
    
    @FXML
    private Label lblAscii;
    
    @FXML
    private TextField[] tfBlocks = null;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	hg.editSaved = false;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	hg.editSaved = true;
    	
    	hg.editBytes = new byte[blockSize];
    	
    	for (int i = 0; i < blockSize; i++) {
    		TextField tf = tfBlocks[i];
    		if (tf.isDisabled() == true)
    			continue;
    		
    		int v = Integer.parseInt(tf.getText(), 16);
    		hg.editBytes[i] = (byte)(v & 0xff);
    	}
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblBlock = new Label();
		lblBlock.setStyle("-fx-font-size: 15px; -fx-border-color: black; -fx-font-family: SanSerif;");
		lblBlock.setPrefWidth(80.0);
		
		hbox.getChildren().add(lblBlock);
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}
	
	public void setSize(int size) {
		blockSize = size;
		
		tfBlocks = new TextField[blockSize];
	}
	
	public void setLine(String line) {
		this.line = line;
		
		String[] a = line.split("\\s+");
		int arrSize = a.length -1;
		lblBlock.setText(a[0]);
		
		lblAscii.setText(a[arrSize]);
		
		int offset = 1;
		for (int i = 0; i < (blockSize / 2); i++) {
			TextField tf = new TextField();
			tf.setPrefWidth(40.0);
			tf.setStyle("-fx-font-size: 14px; -fx-font-family: SanSerif;");
			
            setFilter(tf);
            tf.setText(a[offset++]);
            
			tfBlocks[i] = tf;
		}
		
		if (blockSize == 16)
			offset = 10;
		else
			offset = 18;
		for (int i = (blockSize / 2); i < blockSize; i++) {
			TextField tf = new TextField();
			tf.setPrefWidth(40.0);
			tf.setStyle("-fx-font-size: 14px; -fx-font-family: SanSerif;");
			setFilter(tf);
			if (offset < arrSize)
				tf.setText(a[offset++]);
			else
				tf.setDisable(true);
			tfBlocks[i] = tf;
		}
		
		for (int i = 0; i < blockSize; i++)
			hbox.getChildren().add(tfBlocks[i]);
	}
	
	public String getLine() {
		return line;
	}
	
	private void setFilter(TextField tf) {
		UnaryOperator<TextFormatter.Change> filter = change -> change.getControlNewText().matches("[0-3]?\\p{XDigit}{0,3}") ? change : null;

		StringConverter<Integer> converter = new StringConverter<Integer>() {

		    @Override
		    public String toString(Integer object) {
		        return object == null ? "" : String.format("%02x", object);
		    }

		    @Override
		    public Integer fromString(String string) {
		        return string == null || string.isEmpty() ? null : Integer.parseInt(string, 16);
		    }

		};

		TextFormatter<Integer> formatter = new TextFormatter<>(converter, null, filter);
		tf.setTextFormatter(formatter);
		
		tf.setOnKeyReleased((e) -> {
			if (e.getCode() == KeyCode.ENTER) {
				String v = tf.getText();
				if (v.isBlank() == false) {
					String ascii = null;
					
					for (int i = 0; i < hg.blockSize; i++) {
						TextField tf2 = tfBlocks[i];
						if (tf2.isDisable() == true)
							continue;
						
						byte b = (byte)Integer.parseInt(tf2.getText(), 16);
						if (ascii == null) {
							if (Character.isAlphabetic(b) == true)
								ascii = String.format("%c", b);
							else
								ascii = ".";
						} else {
							if (Character.isAlphabetic(b) == true)
								ascii += String.format("%c", b);
							else
								ascii += ".";
						}
					}
					
					lblAscii.setText(ascii);
				}
			}
		});
	}
}