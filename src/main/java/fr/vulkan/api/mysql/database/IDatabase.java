package fr.vulkan.api.mysql.database;

import fr.vulkan.api.mysql.table.ITable;

public interface IDatabase {

	ITable getTable(String name);
	
	boolean dropTable(String name);
	
	boolean truncateTable(String name);
	
	boolean connect();
	
	boolean close();
	
	boolean status();
}
