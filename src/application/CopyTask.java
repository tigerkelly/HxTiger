package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class CopyTask extends Task<Long> {
	
	private File in = null;
	private File out = null;
	private Stage stage = null;
	
	public CopyTask(File in, File out, Stage stage) {
		this.in = in;
		this.out = out;
		this.stage = stage;
	}

	@Override
	protected Long call() throws Exception {
		long value = 0;
		
		try {
	        FileInputStream fis  = new FileInputStream(in);
	        FileOutputStream fos = new FileOutputStream(out);
	        long fSize = in.length();
	        byte[] buf = new byte[8192];
	        int i = 0;
	        while((i=fis.read(buf))!=-1) {
	            fos.write(buf, 0, i);
	            value += i;
	            
	            updateProgress(value, fSize);
	        }
	        fis.close();
	        fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (stage != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					stage.close();
				}
			});
		}
		
		return value;
	}

}
