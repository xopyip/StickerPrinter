package pl.baluch.stickerprinter.elements.children;

import javafx.scene.text.Font;
import pl.baluch.stickerprinter.elements.children.base.TextBase;


public class Text extends TextBase<javafx.scene.text.Text> {

    public Text() {
        super(javafx.scene.text.Text::new);
    }

    @Override
    protected void setFont(javafx.scene.text.Text node, Font font) {
        node.setFont(font);
    }

    @Override
    protected void setText(javafx.scene.text.Text node, String s) {
        node.setText(s);
    }
}
