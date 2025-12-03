module org.tyler.passprotector.passprotector {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires java.logging;
    requires com.google.gson;


    opens org.tyler.passprotector.passprotector to javafx.fxml;
    exports org.tyler.passprotector.passprotector;
}