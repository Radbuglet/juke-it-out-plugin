package net.coopfury.JukeItOut.helpers.virtualCommand;

import net.coopfury.JukeItOut.Constants;

public final class VirtCommandUtils {
    public static String formatUsageStart(ArgumentList list) {
        return Constants.message_usage_top_start + list.getLeftStr(true);
    }
}
