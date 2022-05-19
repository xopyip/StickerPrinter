package pl.baluch.stickerprinter.windows.main;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import pl.baluch.stickerprinter.AppMain;
import pl.baluch.stickerprinter.events.UpdateStatusBarEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class StatusBarController implements Initializable {
    @FXML
    private Label leftStatus;
    @FXML
    private Label rightStatus;

    @Subscribe
    public void onStickerUpdateClicked(UpdateStatusBarEvent event) {
        switch (event.side()){
            case LEFT:
                leftStatus.setText(event.text());
                break;
            case RIGHT:
                rightStatus.setText(event.text());
                break;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AppMain.EVENT_BUS.register(this);
    }
}
