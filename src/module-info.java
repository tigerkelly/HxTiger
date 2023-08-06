module hxTiger {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.base;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	requires bigdoc;
	
	opens application to javafx.base, javafx.graphics, javafx.fxml;
}
