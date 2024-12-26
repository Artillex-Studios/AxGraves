package com.artillexstudios.axgraves.config;

import com.artillexstudios.axapi.config.YamlConfiguration;
import com.artillexstudios.axapi.config.annotation.Comment;
import com.artillexstudios.axapi.config.annotation.ConfigurationPart;
import com.artillexstudios.axapi.config.annotation.Header;
import com.artillexstudios.axapi.config.annotation.PostProcess;
import com.artillexstudios.axapi.libs.snakeyaml.DumperOptions;
import com.artillexstudios.axapi.utils.YamlUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Header("DOCUMENTATION: https://docs.artillex-studios.com/axgraves.html")
public class Lang implements ConfigurationPart {
    private static final Lang INSTANCE = new Lang();

    public static List<String> hologram = List.of(
            "&#FF0000☠ &#FF0000&l%player% &#FF0000☠",
            "&#00CC00⌛ &#00FF00%despawn-time% &#00CC00⌛",
            "&#00AAFF%item% &fɪᴛᴇᴍ &8| &#00AAFF%xp% &fxᴘ"
    );

    public static List<String> help = List.of(
            " ",
            "&#FF00FF&lAxGraves &7» ",
            " &7- &f/axgraves reload &7| &#FF00FFReload plugin",
            " &7- &f/axgraves list &7| &#FF00FFList spawned graves",
            " "
    );

    public static String guiName = "&0%player%'s Grave";

    public static String unknownPlayer = "Unknown";

    public static class Reload implements ConfigurationPart {
        public static String success = "&#33FF33Plugin successfully reloaded!";

        public static String failed = "&#FF3333Failed to reload the plugin! Something is wrong in the &f%file%&#FF3333 file, look in the console or use a yaml validator to fix the errors!";
    }

    public static class Interact implements ConfigurationPart {
        public static String notYourGrave = "&#FFAAFFYou can only open your own grave!";
    }

    public static class DeathMessage implements ConfigurationPart {
        public static boolean enabled = false;

        public static String message = "&#FFAAAAYou have died at &f%world% %x%, %y%, %z%&#FFAAAA!";
    }

    public static class GraveList implements ConfigurationPart {
        public static String header = "&#FF00FF&l=== GRAVES ===";

        public static String grave = "&#FF00FF[TP] &#FFAAFF%player% &7- &#FFAAFF%world% %x%, %y%, %z% &7(%time%)";

        public static String noGraves = "&#FFAAFFThere are no graves!";
    }

    public static String updateNotifier = "&#FF88FFThere is a new version of AxGraves available! &#DDDDDD(&#FFFFFFcurrent: &#FF0000%current% &#DDDDDD| &#FFFFFFlatest: &#00FF00%latest%&#DDDDDD)";

    @Comment("do not edit")
    public static int version = 1;

    private YamlConfiguration config = null;
    private static Path path;

    public static void setup(Path p) {
        path = p;
        reload();
    }

    public static boolean reload() {
        return INSTANCE.refreshConfig();
    }

    private boolean refreshConfig() {
        if (Files.exists(path) && !YamlUtils.suggest(path.toFile())) {
            return false;
        }

        if (this.config == null) {
            this.config = YamlConfiguration.of(path, Config.class)
                    .configVersion(1, "version")
                    .withDumperOptions(options -> {
                        options.setPrettyFlow(true);
                        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    }).build();
        }

        this.config.load();
        return true;
    }

    @PostProcess
    public static void postProcess() {
    }
}
