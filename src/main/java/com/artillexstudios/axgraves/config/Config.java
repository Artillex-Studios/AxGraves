package com.artillexstudios.axgraves.config;

import com.artillexstudios.axapi.config.YamlConfiguration;
import com.artillexstudios.axapi.config.annotation.Comment;
import com.artillexstudios.axapi.config.annotation.ConfigurationPart;
import com.artillexstudios.axapi.config.annotation.PostProcess;
import com.artillexstudios.axapi.libs.snakeyaml.DumperOptions;
import com.artillexstudios.axapi.utils.YamlUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Config implements ConfigurationPart {
    private static final Config INSTANCE = new Config();

    public static String prefix = "&#FF00FF&lAxGraves &7» ";

    @Comment("""
            how fast should the graves disappear
            set to -1 to make graves last until the next server restart
            in seconds
            """)
    public static int despawnTimeSeconds = 180;

    @Comment("""
            should graves drop all items on the ground when they expire?
            if false: items will be removed
            """)
    public static boolean dropItems = true;

    @Comment("""
            if drop-items is true, should the dropped items have a velocity? (so should they fly around when dropped or stay in one block?)
            """)
    public static boolean droppedItemVelocity = true;

    @Comment("""
            should players have the ability to take all items from a death chest by shift right clicking?
            """)
    public static boolean enableInstantPickup = true;

    @Comment("""
            only works if enable-instant-pickup is true
            this makes it that only the owner of the grave will be able to instantly pick it up
            """)
    public static boolean instantPickupOnlyOwn = false;

    @Comment("""
            you should disable this if you want keep-inventory to work
            """)
    public static boolean overrideKeepInventory = true;

    @Comment("""
            how high should grave holograms spawn?
            only applies to new graves
            """)
    public static float hologramHeight = 0.75f;

    @Comment("""
            how high should the player head be?
            in some versions on some clients may see the heads glitched in the ground
            use this option OR viaversion's hologram-y setting to correct it
            only applies to new graves
            """)
    public static float headHeight = -1.2f;

    public static List<String> disabledWorlds = List.of(
            "blacklisted_world"
    );

    @Comment("""
            worlds where graves won't spawn
            this is case-sensitive
            true: the head can face in all the 360 degrees
            false: the head can face in only 4 directions (north, east, south, west)
            """)
    public static boolean rotateHead360 = true;

    public static class AutoRotation implements ConfigurationPart {
        @Comment("""
                should the head rotate
                """)
        public static boolean enabled = false;

        public static float speed = 10.0f;
    }

    @Comment("""
            true: only the person who died and people with axgraves.admin can open the grave
            false: everyone can open the grave
            """)
    public static boolean interactOnlyOwn = false;

    @Comment("""
            should the player lose any xp on death?
            this is a percentage, so 0.5 would be 50% xp
            """)
    public static float xpKeepPercentage = 1.0f;

    @Comment("""
            should the plugin store XP in graves?
            if disabled, XP will be dropped on the ground
            """)
    public static boolean storeXp = true;

    public static List<String> graveItemOrder = List.of(
            "ARMOR",
            "HAND",
            "OFFHAND"
    );

    @Comment("""
            what order should items be put in the grave?
            all the other items will be put AFTER these
            values: ARMOR, HAND, OFFHAND
            should the armor parts be auto equipped?
            """)
    public static boolean autoEquipArmor = true;

    @Comment("""
            how many graves can a single player have at the same time?
            if the limit is reached, the first grave will be removed
            set to -1 to disable
            """)
    public static int graveLimit = -1;

//    public static class SpawnHeightLimits implements ConfigurationPart {
//        public static class World implements ConfigurationPart {
//            public static int min = -64;
//            public static int max = 319;
//        }
//
//        public static class WorldNether implements ConfigurationPart {
//            public static int min = 0;
//            public static int max = 319;
//        }
//
//        public static class WorldTheEnd implements ConfigurationPart {
//            public static int min = 0;
//            public static int max = 319;
//        }
//    }

    @Comment("""
            you can add any amount of worlds
            if you don't define a world, the plugin will use the default world limits of the dimension
            """)
    public static Map<String, Map<String, Integer>> spawnHeightLimits = Map.of(
            "world", Map.of(
                    "min", -64,
                    "max", 319
            ),
            "world_nether", Map.of(
                    "min", 0,
                    "max", 319
            ),
            "world_the_end", Map.of(
                    "min", 0,
                    "max", 319
            )
    );

    @Comment("""
            if players die from any of the following, no graves will be spawned
            list of valid values: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html
            case sensitive
            """)
    public static List<String> blacklistedDeathCauses = List.of(
            "EXAMPLE_DEATH_CAUSE"
    );

    @Comment("""
            if players die from any of the following, no graves will be spawned
            list of valid values: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html
            case sensitive
            should the grave disappear when all items get taken out of it?
            """)
    public static boolean despawnWhenEmpty = true;

    public static class SaveGraves implements ConfigurationPart {
        @Comment("""
                should graves be saved if the server stops?
                """)
        public static boolean enabled = false;

        @Comment("""
                how often should the graves be saved?
                set to -1 to make it only save on shutdown (it might not save if there is a crash)
                """)
        public static int autoSaveSeconds = 30;
    }

    @Comment("""
                items that will be removed on death and will not show up in graves
                """)
    public static Map<String, Map<String, String>> blacklistedItems = Map.of(
            "1", Map.of(
                    "material", "barrier",
                    "name-contains", "Banned item's name"
            )
    );

    @Comment("""
                should be plugin notify you if there is a new update?
                """)
    public static class UpdateNotifier implements ConfigurationPart {
        @Comment("""
                if enabled, it will display the message in the console
                """)
        public static boolean enabled = false;

        @Comment("""
                if enabled, it will broadcast the update message to all players who have the <plugin-name>.update-notify permission
                """)
        public static boolean onJoin = true;
    }

    @Comment("do not edit")
    public static int version = 13;

    private YamlConfiguration config = null;
    private static Path path;

    public static void setup(Path p) {
        path = p;
        reload();
    }

    public static boolean reload() {
        return INSTANCE.refreshConfig();
    }

    private boolean refremshConfig() {
        if (Files.exists(path) && !YamlUtils.suggest(path.toFile())) {
            return false;
        }

        if (this.config == null) {
            this.config = YamlConfiguration.of(path, Config.class)
                    .configVersion(13, "version")
                    .withDumperOptions(options -> {
                        options.setPrettyFlow(true);
                        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    }).build();
        }

        this.config.load();
        this.config.save();
        System.out.println(config.dumpInternalData());
        return true;
    }

    @PostProcess
    public static void postProcess() {
    }
}
