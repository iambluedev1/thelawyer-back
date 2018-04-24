package fr.iambluedev.thelawyer.mysql.connector;

import fr.vulkan.api.mysql.connector.IConnector;

public class MySQLConnector implements IConnector {

	@Override
	public String getName() {
		return "mysql";
	}

	@Override
	public String getDriverPath() {
		return  "com.mysql.jdbc.Driver";
	}

}
