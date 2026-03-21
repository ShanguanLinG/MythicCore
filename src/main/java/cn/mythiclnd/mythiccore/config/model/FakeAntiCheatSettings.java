package cn.mythiclnd.mythiccore.config.model;

public record FakeAntiCheatSettings(boolean enabled) {
    public boolean isEnabled() {
        return enabled;
    }
}
