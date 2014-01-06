package net.tbnr.util.json;

public enum JSONChatClickType {
    RUN_COMMAND("run_command"),
    SUGGEST_COMMAND("suggest_command"),
    OPEN_URL("open_url");
    private final String type;

    JSONChatClickType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }
}
