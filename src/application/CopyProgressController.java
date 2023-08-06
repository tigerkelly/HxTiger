package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CopyProgressController implements Initializable {
	
//	private HxGlobal hg = HxGlobal.getInstance();

    @FXML
    private AnchorPane aPane;

    @FXML
    private ProgressBar pb;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}
	
	public void setFileInOut(File in, File out) {
		
		CopyTask ct = new CopyTask(in, out, (Stage)aPane.getScene().getWindow());
		pb.progressProperty().bind(ct.progressProperty());
		
		Thread th = new Thread(ct);
		th.setDaemon(true);
		th.start();
	}

}