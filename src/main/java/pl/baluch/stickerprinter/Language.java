package pl.baluch.stickerprinter;

public enum Language {
    Polish("Polski", "pl"),
    English("English", "en");

    private String title;
    private String code;

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
