module com.computernetwork.filetransfer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.computernetwork.filetransfer to javafx.fxml;
    exports com.computernetwork.filetransfer;
    exports com.computernetwork.filetransfer.Model;
    opens com.computernetwork.filetransfer.Model to javafx.fxml;
}