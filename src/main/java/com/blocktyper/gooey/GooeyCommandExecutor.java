package com.blocktyper.gooey;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GooeyCommandExecutor implements CommandExecutor{
	
	GooeyPlugin plugin;
	
	
	
	public GooeyCommandExecutor(GooeyPlugin plugin) {
		super();
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		if (args == null || args.length < 1) {
			printUsage(sender);
			return true;
		}

		String menuKey = args[0];
		Key menuRoot = plugin.key(GooeyPlugin.MENUS_ROOT).__(menuKey);
		String menuName = plugin.getConfig().getString(menuRoot.end(GooeyPlugin.NAME), "");

		if (!plugin.getConfig().contains(menuRoot.val)) {
			printUsage(sender);
			return false;
		}

		List<ItemStack> menuItems = new ArrayList<>();

		for (String key : plugin.getConfig().getConfigurationSection(menuRoot.__(GooeyPlugin.ITEMS_ROOT).val).getKeys(false)) {
			Key itemRoot = plugin.key(menuRoot.end(key));

			int repeat = plugin.getConfig().getInt(itemRoot.end(GooeyPlugin.REPEAT), 1);

			if (repeat < 1) {
				continue;
			}

			for (int iteration = 0; iteration < repeat; iteration++) {
				ItemStack menuItem = getMenuItem(itemRoot, key);
				menuItems.add(menuItem);
			}
		}

		int size = menuItems.size();
		size = size > 0 ? size : 1;

		int rows = (menuItems.size() / GooeyPlugin.INVENTORY_COLUMNS) + (size % GooeyPlugin.INVENTORY_COLUMNS > 0 ? 1 : 0);
		
		plugin.debug("menuName: " + menuName);

		Inventory inventory = Bukkit.createInventory(null, rows * GooeyPlugin.INVENTORY_COLUMNS,
				Invis.prependKey(menuName, menuKey + GooeyPlugin.GOOEY_INVIS_KEY));

		int inventoryIndex = 0;
		for (ItemStack menuItem : menuItems) {
			inventory.setItem(inventoryIndex, menuItem);
			inventoryIndex++;
		}

		Player player = (Player) sender;

		player.openInventory(inventory);

		return true;
	}

	private void printUsage(CommandSender sender) {
		for (String menu : plugin.getConfig().getConfigurationSection(GooeyPlugin.MENUS_ROOT).getKeys(false)) {
			sender.sendMessage("/gui " + menu);
		}
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getMenuItem(Key itemRoot, String itemKey) {

		plugin.debug("itemRoot: " + itemRoot.val);

		String name = plugin.getConfig().getString(itemRoot.end(GooeyPlugin.NAME), null);
		String materialName = plugin.getConfig().getString(itemRoot.end(GooeyPlugin.MATERIAL), null);
		Integer materialData = plugin.getConfig().getInt(itemRoot.end(GooeyPlugin.MATERIAL_DATA), 0);

		plugin.debug("name: " + name);
		plugin.debug("materialName: " + materialName);
		plugin.debug("materialData: " + materialData);

		List<String> info = plugin.getConfig().getStringList(itemRoot.end(GooeyPlugin.INFO));

		Material material = Material.matchMaterial(materialName);

		if (material == Material.AIR) {
			return new ItemStack(material);
		}

		ItemStack menuItem = null;

		if (materialData > 0) {
			menuItem = new ItemStack(material, 1, (short) 500, materialData.byteValue());
		} else {
			menuItem = new ItemStack(material);
		}

		ItemMeta itemMeta = menuItem.getItemMeta();

		if (itemMeta == null) {
			return menuItem;
		}

		if (name != null) {
			itemMeta.setDisplayName(name);
		} else if (plugin.getConfig().contains(itemRoot.end(GooeyPlugin.NAME))) {
			itemMeta.setDisplayName(" ");
		}
		
		if (plugin.getConfig().contains(itemRoot.end(GooeyPlugin.HANDLER))) {
			if(info == null){
				info = new ArrayList<>();
			}
			info.add(0, Invis.encode(itemKey + GooeyPlugin.GOOEY_INVIS_KEY));
		}

		itemMeta.setLore(info);
		
		menuItem.setItemMeta(itemMeta);

		return menuItem;
	}
}
