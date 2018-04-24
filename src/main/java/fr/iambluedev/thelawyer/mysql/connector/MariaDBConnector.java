package fr.iambluedev.thelawyer.mysql.connector;

import fr.vulkan.api.mysql.connector.IConnector;

public class MariaDBConnector implements IConnector {

	@Override
	public String getName() {
		return "mariadb";
	}

	@Override
	public String getDriverPath() {
		return "org.mariadb.jdbc.Driver";
	}

}
