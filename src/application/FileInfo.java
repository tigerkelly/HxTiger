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
