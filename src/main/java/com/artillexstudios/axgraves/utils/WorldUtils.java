package com.artillexstudios.axgraves.utils;

import java.util.List;

public class WorldUtils {

    /**
     * Checks if a world name matches any pattern in the disabled worlds list.
     * Supports wildcard patterns:
     * - *text -> matches any string ending with "text"
     * - text* -> matches any string starting with "text"  
     * - *text* -> matches any string containing "text"
     * - text -> exact match (backwards compatibility)
     *
     * @param worldName the world name to check
     * @param disabledWorlds list of patterns to match against
     * @return true if the world matches any disabled pattern, false otherwise
     */
    public static boolean isWorldDisabled(String worldName, List<String> disabledWorlds) {
        if (worldName == null || disabledWorlds == null) {
            return false;
        }

        for (String pattern : disabledWorlds) {
            if (pattern == null) {
                continue;
            }

            if (matchesPattern(worldName, pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a world name matches a specific pattern.
     *
     * @param worldName the world name to check
     * @param pattern the pattern to match against
     * @return true if the world name matches the pattern, false otherwise
     */
    private static boolean matchesPattern(String worldName, String pattern) {
        // Handle exact match (backwards compatibility)
        if (!pattern.contains("*")) {
            return worldName.equals(pattern);
        }

        // Handle wildcard patterns
        if (pattern.startsWith("*") && pattern.endsWith("*")) {
            // *text* -> contains
            String middle = pattern.substring(1, pattern.length() - 1);
            return worldName.contains(middle);
        } else if (pattern.startsWith("*")) {
            // *text -> ends with
            String suffix = pattern.substring(1);
            return worldName.endsWith(suffix);
        } else if (pattern.endsWith("*")) {
            // text* -> starts with
            String prefix = pattern.substring(0, pattern.length() - 1);
            return worldName.startsWith(prefix);
        }

        // Fallback to exact match if pattern format is unexpected
        return worldName.equals(pattern);
    }
}