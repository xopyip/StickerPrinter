package pl.baluch.stickerprinter;

public class Config {
    private Language locale = Language.English;

    public Language getLocale() {
        return locale;
    }

    public void setLocale(Language locale) {
        this.locale = locale;
    }
}
