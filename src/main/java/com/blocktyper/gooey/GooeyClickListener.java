package com.blocktyper.gooey;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GooeyClickListener implements Listener{
	GooeyPlugin plugin;

	public GooeyClickListener(GooeyPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (event.getInventory() == null || event.getInventory().getName() == null) {
			return;
		}
		String menuKey = plugin.getInvisPrefix(event.getInventory().getName());
		if (menuKey != null) {
			Key menuRoot = plugin.key(GooeyPlugin.MENUS_ROOT).__(menuKey);
			boolean allowPickup = plugin.getConfig().getBoolean(menuRoot.end(GooeyPlugin.ALLOW_PICKUP), false);

			if (!allowPickup) {
				event.setCancelled(true);
			}
			
			ItemStack itemClicked = event.getCurrentItem();
			
			processItemClicked(itemClicked, menuRoot, event);
			
		}
	}

	private void processItemClicked(ItemStack itemClicked, Key menuRoot, InventoryClickEvent event) {
		if(itemClicked == null || itemClicked.getType() == null){
			return;
		}
		
		plugin.debug("processItemClicked: " + itemClicked.getType().name());
		
		if(itemClicked.getItemMeta() == null || itemClicked.getItemMeta().getLore() == null || itemClicked.getItemMeta().getLore().isEmpty()){
			plugin.debug("processItemClicked: " + itemClicked.getType().name() + " - no lore");
			return;
		}
		String firstLoreLine = itemClicked.getItemMeta().getLore().get(0);
		
		if(firstLoreLine == null){
			plugin.debug("processItemClicked: " + itemClicked.getType().name() + " - firstLoreLine null");
			return;
		}
		
		if(!Invis.textContainsKey(firstLoreLine, GooeyPlugin.GOOEY_INVIS_KEY)){
			plugin.debug("processItemClicked: " + itemClicked.getType().name() + " - no invis prefix");
			return;
		}
		
		Key itemKey = menuRoot.__(GooeyPlugin.ITEMS_ROOT).__(plugin.getInvisPrefix(firstLoreLine));
		
		if(!plugin.getConfig().contains(itemKey.val)){
			plugin.debug("processItemClicked: " + itemClicked.getType().name() + " - Item key not in config: " + itemKey.val);
			return;
		}
		
		if(!plugin.getConfig().contains(itemKey.end(GooeyPlugin.HANDLER))){
			plugin.debug("processItemClicked: " + itemClicked.getType().name() + " -No handler in config: " + itemKey.end(GooeyPlugin.HANDLER));
			return;
		}
		
		String handler = plugin.getConfig().getString(itemKey.end(GooeyPlugin.HANDLER));
		
		plugin.debug("handler found: " + handler);
	}
}
