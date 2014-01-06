package net.tbnr.util.json;

public enum JSONChatFormat {

    OBFUSCATED("obfuscated"),
    BOLD("bold"),
    STRIKETHROUGH("strikethrough"),
    UNDERLINED("underlined"),
    ITALIC("italic");

    private final String format;

    JSONChatFormat(String format) {
        this.format = format;
    }

    public String getFormatString() {
        return format;
    }
}
