package pl.baluch.stickerprinter;

public class StickerProperty {
    private String key;
    private String value;

    public StickerProperty(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
