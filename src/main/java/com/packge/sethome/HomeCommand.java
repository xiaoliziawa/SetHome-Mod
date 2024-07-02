package com.packge.sethome;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = HomeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HomeCommand {

    private static final Map<UUID, Map<String, Vec3>> homes = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> setHome(context.getSource(), StringArgumentType.getString(context, "name"))))
                .executes(context -> setHome(context.getSource(), "default")));

        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> teleportHome(context.getSource(), StringArgumentType.getString(context, "name"))))
                .executes(context -> teleportHome(context.getSource(), "default")));

        dispatcher.register(Commands.literal("delhome")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> deleteHome(context.getSource(), StringArgumentType.getString(context, "name"))))
                .executes(context -> deleteHome(context.getSource(), "default")));
    }

    private static int setHome(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID playerUUID = player.getUUID();
        Vec3 pos = player.position();

        Map<String, Vec3> playerHomes = homes.computeIfAbsent(playerUUID, k -> new HashMap<>());

        if (playerHomes.size() >= HomeConfig.MAX_HOMES.get() && !playerHomes.containsKey(name)) {
            source.sendFailure(Component.literal("你可以设置的家数量为： (" + HomeConfig.MAX_HOMES.get() + ")."));
            return 0;
        }

        playerHomes.put(name, pos);

        source.sendSuccess(Component.literal("家 '" + name + "' 已经设置!"), true);
        return 1;
    }

    private static int teleportHome(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID playerUUID = player.getUUID();

        if (!homes.containsKey(playerUUID) || !homes.get(playerUUID).containsKey(name)) {
            source.sendFailure(Component.literal("家 '" + name + "' 未找到。"));
            return 0;
        }

        Vec3 homePos = homes.get(playerUUID).get(name);
        player.teleportTo(homePos.x, homePos.y, homePos.z);

        source.sendSuccess(Component.literal("传送到家 '" + name + "'。"), true);
        return 1;
    }

    private static int deleteHome(CommandSourceStack source, String name) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        UUID playerUUID = player.getUUID();

        if (!homes.containsKey(playerUUID) || !homes.get(playerUUID).containsKey(name)) {
            source.sendFailure(Component.literal("家 '" + name + "' 未找到"));
            return 0;
        }

        homes.get(playerUUID).remove(name);

        source.sendSuccess(Component.literal("家 '" + name + "' 已删除!"), true);
        return 1;
    }
}
