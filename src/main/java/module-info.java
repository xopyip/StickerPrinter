module pl.baluch.stickerprinter {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    exports pl.baluch.stickerprinter;
    exports pl.baluch.stickerprinter.plugins;
    exports pl.baluch.stickerprinter.data;
}