package net.tbnr.util.json;

public enum JSONChatHoverType {

    SHOW_TEXT("show_text"),
    SHOW_ITEM("show_item"),
    SHOW_ACHIEVEMENT("show_achievement");

    private final String type;

    JSONChatHoverType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }
}
