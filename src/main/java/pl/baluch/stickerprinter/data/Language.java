package pl.baluch.stickerprinter.data;

public enum Language {
    Polish("Polski", "pl"),
    English("English", "en");

    private final String title;
    private final String code;

    Language(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }
}
