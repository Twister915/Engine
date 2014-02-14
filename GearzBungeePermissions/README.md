Permissions Documentation
=====
Documentation on the usage of the permissions system.

Commands:
=====
  * /player [player]
     * reset - Resets a player to the default state
	 * set [permission] [value] - Sets a permission to a player
	 * remove [permission] - Removes a permission
	 * check [permission] - Checks a permission for a player
	 * perms - List the permissions of a player, includes all groups
	 * setgroup - Sets the player's group
	 * removegroup - Remove the player's group
	 * prefix [prefix] - Sets the player's prefix
	 * suffix [suffix] - Sets the player's suffix
	 * tabcolor [tabcolor] - Sets the player's tab color
	 * namecolor [namecolor] - Sets the player's name color
  * /group [group]
     * create - Creates a new group
	 * delete - Deletes a group
	 * set [permission] [value] - Sets a permission to a group
	 * unset [permission] - Removes a permission from a group
	 * perms - Lists a group's permissions
	 * prefix [prefix] - Sets a group's prefix
	 * suffix [suffix] - Sets a group's suffix
	 * tabcolor [tabcolor] - Sets a group's tab color
	 * namecolor [namecolor] - Set a group's name color
	 * addinheritance [group] - Adds the group as an inheritance
	 * removeinheritance [group] - Removes the group as an inheritance
  * /permissions
     * reload - Reloads the permissions from the database
	 
Notes:
=====
  * Commands only exist on the Bukkit side
  * Player specific metadata override group ones.
	 
	 
	 
