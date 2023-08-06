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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class FileInfo {

	private File fd;
	private long fileSize;
	private long blockNum;			// number of 16 byte blocks.
	private long lastBlock;
	private int blockSize;
	private RandomAccessFile raf;
	private boolean backup;
	private String lastPattern;
	private TextField tfValue;
	private Slider slider;
	private ComboBox<Long> choice;
	private Label searchStatus;
	private List<Long> positions;
	private long lastPosition;
	
	public FileInfo(File fd) {
		this.fd = fd;
		
		try {
			fileSize = fd.length();
			raf = new RandomAccessFile(fd.getAbsolutePath(), "rw");
		} catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public File getFd() {
		return fd;
	}

	public void setFd(File fd) {
		this.fd = fd;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public RandomAccessFile getRaf() {
		return raf;
	}

	public void setRaf(RandomAccessFile raf) {
		this.raf = raf;
	}
	
	public long getBlockNum() {
		return blockNum;
	}
	
	public long getLastBlock() {
		return lastBlock;
	}
	
	public void setLastBlock(long lastBlock) {
		this.lastBlock = lastBlock;
	}
	
	public byte[] loadBlock(int blockNum) throws IOException {
		byte[] buf = new byte[16];
		
		raf.seek(blockNum * 16);
		
		raf.read(buf);
		
		return buf;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
		blockNum = fileSize / blockSize;
		if ((fileSize % blockSize) > 0)
			blockNum++;
	}
	
	public boolean isBackup() {
		return backup;
	}
	
	public void setBackup(boolean backup) {
		this.backup = backup;
	}

	public TextField getTfValue() {
		return tfValue;
	}

	public void setTfValue(TextField tfValue) {
		this.tfValue = tfValue;
	}

	public Slider getSlider() {
		return slider;
	}

	public void setSlider(Slider slider) {
		this.slider = slider;
	}

	public ComboBox<Long> getChoice() {
		return choice;
	}

	public void setChoice(ComboBox<Long> choice) {
		this.choice = choice;
	}

	public String getLastPattern() {
		return lastPattern;
	}

	public void setLastPattern(String lastPattern) {
		this.lastPattern = lastPattern;
	}

	public Label getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(Label searchStatus) {
		this.searchStatus = searchStatus;
	}

	public List<Long> getPositions() {
		return positions;
	}

	public void setPositions(List<Long> positions) {
		this.positions = positions;
	}

	public long getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(long lastPosition) {
		this.lastPosition = lastPosition;
	}
}
