package top.soybottle.slotcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class SlotCommand implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(CommandManager.literal("slot")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(CommandManager.argument("player", EntityArgumentType.players())
					.then(CommandManager.argument("slot", IntegerArgumentType.integer(0, 8))
						.executes(context -> {
							Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
							int slot = IntegerArgumentType.getInteger(context, "slot");
							for (ServerPlayerEntity player : players) {
								player.inventory.selectedSlot = slot;
								player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.inventory.selectedSlot));
							}
							return players.size();
						})))
			);
		});
	}
}
