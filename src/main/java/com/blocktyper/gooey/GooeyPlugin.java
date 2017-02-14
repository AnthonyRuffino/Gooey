package com.blocktyper.gooey;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GooeyPlugin extends JavaPlugin implements CommandExecutor, Listener {

	private boolean debug = false;

	public static final String GOOEY_INVIS_KEY = "#GOOEY_";

	public static final String MENUS_ROOT = "menus";

	public static final String ITEMS_ROOT = "items";

	public static final String ALLOW_PICKUP = "allow-pickup";

	public static final String NAME = "name";
	public static final String MATERIAL = "material";
	public static final String MATERIAL_DATA = "material-data";
	public static final String INFO = "info";
	public static final String REPEAT = "repeat";

	public static final int INVENTORY_COLUMNS = 9;

	/*
	 * for(String cEnchantment : enchants.keySet()) {
	 * map.put(CardboardEnchantment.fromName(cEnchantment).unbox(),
	 * enchants.get(cEnchantment)); }
	 * 
	 * item.addUnsafeEnchantments(map);
	 */

	public GooeyPlugin() {
		super();
		debug = getConfig().getBoolean("debug", false);
	}

	public void onEnable() {
		super.onEnable();
		
		if(!getConfig().getBoolean("dont-load-example", false)){
			saveDefaultConfig();
			getConfig().options().copyDefaults(true);
			saveConfig();
			reloadConfig();
		}

		

		getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("gui").setExecutor(this);
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
		Key menuRoot = key(MENUS_ROOT).__(menuKey);
		String menuName = getConfig().getString(menuRoot.end(NAME), "");

		if (!getConfig().contains(menuRoot.val)) {
			printUsage(sender);
			return false;
		}

		List<ItemStack> menuItems = new ArrayList<>();

		for (String key : getConfig().getConfigurationSection(menuRoot.__(ITEMS_ROOT).val).getKeys(false)) {
			Key itemRoot = key(menuRoot.end(key));

			int repeat = getConfig().getInt(itemRoot.end(REPEAT), 1);

			if (repeat < 1) {
				continue;
			}

			for (int iteration = 0; iteration < repeat; iteration++) {
				ItemStack menuItem = getMenuItem(itemRoot);
				menuItems.add(menuItem);
			}
		}

		int size = menuItems.size();
		size = size > 0 ? size : 1;

		int rows = (menuItems.size() / INVENTORY_COLUMNS) + (size % INVENTORY_COLUMNS > 0 ? 1 : 0);
		
		debug("menuName: " + menuName);

		Inventory inventory = Bukkit.createInventory(null, rows * INVENTORY_COLUMNS,
				Invis.prependKey(menuName, menuKey + GOOEY_INVIS_KEY));

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
		for (String menu : getConfig().getConfigurationSection(MENUS_ROOT).getKeys(false)) {
			sender.sendMessage("/gui " + menu);
		}
	}

	@SuppressWarnings("deprecation")
	private ItemStack getMenuItem(Key itemRoot) {

		debug("itemRoot: " + itemRoot.val);

		String name = getConfig().getString(itemRoot.end(NAME), null);
		String materialName = getConfig().getString(itemRoot.end(MATERIAL), null);
		Integer materialData = getConfig().getInt(itemRoot.end(MATERIAL_DATA), 0);

		debug("name: " + name);
		debug("materialName: " + materialName);
		debug("materialData: " + materialData);

		List<String> info = getConfig().getStringList(itemRoot.end(INFO));

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
		} else if (getConfig().contains(itemRoot.end(NAME))) {
			itemMeta.setDisplayName(" ");
		}

		itemMeta.setLore(info);
		menuItem.setItemMeta(itemMeta);

		return menuItem;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (event.getInventory() == null || event.getInventory().getName() == null) {
			return;
		}
		if (Invis.textContainsKey(event.getInventory().getName(), GOOEY_INVIS_KEY)) {

			String menuWithPrefix = Invis.decode(event.getInventory().getName());

			String menuKey = menuWithPrefix.substring(0, menuWithPrefix.indexOf(GOOEY_INVIS_KEY));

			Key menuRoot = key(MENUS_ROOT).__(menuKey);
			boolean allowPickup = getConfig().getBoolean(menuRoot.end(ALLOW_PICKUP), false);

			if (!allowPickup) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * 
	 * @param val
	 * @return
	 */
	private Key key(String val) {
		Key key = new Key();
		key.val = val;
		return key;
	}

	private void debug(String msg) {
		if (debug) {
			getLogger().info(msg);
		}
	}

}
