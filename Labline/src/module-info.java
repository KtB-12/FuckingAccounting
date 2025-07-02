module Labline {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires kernel;
	requires layout;
	requires java.desktop;
    
    opens application to javafx.base, javafx.graphics, javafx.fxml;
    exports application;
}