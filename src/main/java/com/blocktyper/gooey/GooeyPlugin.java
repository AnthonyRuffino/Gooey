package com.blocktyper.gooey;

import org.bukkit.plugin.java.JavaPlugin;

public class GooeyPlugin extends JavaPlugin {

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
	public static final String HANDLER = "handler";

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

		if (!getConfig().getBoolean("dont-load-example", false)) {
			saveDefaultConfig();
			getConfig().options().copyDefaults(true);
			saveConfig();
			reloadConfig();
		}

		getServer().getPluginManager().registerEvents(new GooeyClickListener(this), this);
		this.getCommand("gui").setExecutor(new GooeyCommandExecutor(this));
	}

	

	/**
	 * 
	 * @param val
	 * @return
	 */
	Key key(String val) {
		Key key = new Key();
		key.val = val;
		return key;
	}

	void debug(String msg) {
		if (debug) {
			getLogger().info(msg);
		}
	}
	
	String getInvisPrefix(String input){
		String output = null;
		if(Invis.textContainsKey(input, GooeyPlugin.GOOEY_INVIS_KEY)){
			input = Invis.decode(input);
			output = input.substring(0, input.indexOf(GooeyPlugin.GOOEY_INVIS_KEY));
		}
		return output;
	}

}
