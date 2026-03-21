package cn.mythiclnd.mythiccore.config.model;

public record MainSettings(
        TabCompleteSettings tabComplete,
        PluginsCommandSettings pluginsCommand,
        FakeAntiCheatSettings fakeAntiCheat
) {
    public TabCompleteSettings getTabComplete() {
        return tabComplete;
    }

    public PluginsCommandSettings getPluginsCommand() {
        return pluginsCommand;
    }

    public FakeAntiCheatSettings getFakeAntiCheat() {
        return fakeAntiCheat;
    }
}
