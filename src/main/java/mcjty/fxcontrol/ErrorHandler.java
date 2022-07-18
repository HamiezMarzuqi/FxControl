package mcjty.fxcontrol;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashSet;
import java.util.Set;

public class ErrorHandler {

    private static final Set<String> errors = new HashSet<>();

    public static void clearErrors() {
        errors.clear();
    }

    // Publish an error and notify all players of that error
    public static void error(String message) {
        errors.add(message);
        FxControl.setup.getLogger().error(message);
        // Notify all logged in players
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal(ChatFormatting.RED + "FxControl Error: " + ChatFormatting.GOLD + message));
            }
        }
    }

    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        for (String error : errors) {
            event.getEntity().sendSystemMessage(Component.literal(ChatFormatting.RED + "FxControl Error: " + ChatFormatting.GOLD + error));
        }
    }
}
