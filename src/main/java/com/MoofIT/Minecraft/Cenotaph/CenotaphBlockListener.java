package com.MoofIT.Minecraft.Cenotaph;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CenotaphBlockListener implements Listener {
	private Cenotaph plugin;

	public CenotaphBlockListener(Cenotaph instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		Player p = event.getPlayer();

		if (b.getType() == Material.WALL_SIGN)
		{
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign)b.getState().getData();
			TombBlock tBlock = Cenotaph.tombBlockList.get(b.getRelative(signData.getAttachedFace()).getLocation());
			if (tBlock == null) return;

			if (tBlock.getLocketteSign() != null) {
				Sign sign = (Sign)b.getState();
				event.setCancelled(true);
				sign.update();
				return;
			}
		}

		if (b.getType() != Material.CHEST && b.getType() != Material.SIGN_POST) return;

		TombBlock tBlock = Cenotaph.tombBlockList.get(b.getLocation());
		if (tBlock == null) return;

		if (plugin.noDestroy && !p.hasPermission("cenotaph.admin")) {
			plugin.sendMessage(p, "You cannot break this cenotaph..");
			event.setCancelled(true);
			return;
		}

		if (plugin.lwcPlugin != null && plugin.lwcEnable && tBlock.getLwcEnabled()) {
			if (tBlock.getOwner().equals(p.getName()) || p.hasPermission("cenotaph.admin")) {
				plugin.deactivateLWC(tBlock, true);
			} else {
				event.setCancelled(true);
				return;
			}
		}
		plugin.removeTomb(tBlock, true);
		Player owner = plugin.getServer().getPlayer(tBlock.getOwner());
		if (owner != null) plugin.sendMessage(owner, "Your cenotaph has been destroyed by " + p.getName() + "!");
	}
}
