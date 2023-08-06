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
