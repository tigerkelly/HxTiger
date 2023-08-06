package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class BackupController implements Initializable {
	
	private HxGlobal hg = HxGlobal.getInstance();

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCopy;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblTitle;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	hg.backupFile = false;
    	
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnCopy(ActionEvent event) {
    	hg.backupFile = true;
    	
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}
	
	public void setMessage(String msg) {
		lblMessage.setText(msg);
	}

}