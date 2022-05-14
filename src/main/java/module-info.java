module pl.baluch.stickerprinter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires java.desktop;

    exports pl.baluch.stickerprinter;
    exports pl.baluch.stickerprinter.plugins;
    exports pl.baluch.stickerprinter.data;
    exports pl.baluch.stickerprinter.windows;
    exports pl.baluch.stickerprinter.elements;

    opens pl.baluch.stickerprinter to javafx.fxml, com.google.gson;
    opens pl.baluch.stickerprinter.data to javafx.fxml, com.google.gson;
    opens pl.baluch.stickerprinter.elements to javafx.fxml, com.google.gson;
    opens pl.baluch.stickerprinter.windows to javafx.fxml;
    exports pl.baluch.stickerprinter.elements.containers;
    opens pl.baluch.stickerprinter.elements.containers to com.google.gson, javafx.fxml;
    exports pl.baluch.stickerprinter.elements.children;
    opens pl.baluch.stickerprinter.elements.children to com.google.gson, javafx.fxml;
}