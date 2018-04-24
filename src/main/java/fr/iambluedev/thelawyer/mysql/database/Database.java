package fr.iambluedev.thelawyer.mysql.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.mysql.connector.MySQLConnector;
import fr.iambluedev.thelawyer.mysql.request.RequestHandler;
import fr.iambluedev.thelawyer.mysql.table.Table;
import fr.vulkan.api.mysql.connector.IConnector;
import fr.vulkan.api.mysql.database.IDatabase;
import fr.vulkan.api.mysql.table.ITable;
import lombok.Getter;

@Getter
public class Database implements IDatabase{

	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Getter
	private static Database instance;
	
	private Connection connection;
	private RequestHandler requestHandler;
	private String host;
    private String user;
	private String password;
	private String database;
	private Integer port;
	private boolean connected;
	private IConnector connector;
	
	public Database(String host, String database, String user, String password) {
		this.database = database;
        this.user = user;
        this.password = password;
        this.port = 3306;
        this.requestHandler = new RequestHandler(this);
        this.connector = new MySQLConnector();
        this.host = this.formaHost(host);
        instance = this;
	}
	
	public Database(String host, String database, String user, String password, Integer port) {
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        this.requestHandler = new RequestHandler(this);
        this.connector = new MySQLConnector();
        this.host = this.formaHost(host);
        instance = this;
	}
	
	public Database(String host, String database, String user, String password, IConnector connector) {
		this.database = database;
        this.user = user;
        this.password = password;
        this.port = 3306;
        this.requestHandler = new RequestHandler(this);
        this.connector = connector;
        this.host = this.formaHost(host);
        instance = this;
	}
	
	public Database(String host, String database, String user, String password, Integer port, IConnector connector) {
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        this.requestHandler = new RequestHandler(this);
        this.connector = connector;
        this.host = this.formaHost(host);
        instance = this;
	}

	@Override
	public ITable getTable(String name) {
		return new Table(this, name);
	}

	@Override
	public boolean dropTable(String name) {
		return this.requestHandler.executeUpdate("DROP TABLE `" + name + "`");
	}

	@Override
	public boolean truncateTable(String name) {
		return this.requestHandler.executeUpdate("TRUNCATE TABLE `" + name + "`");
	}

	@Override
	public boolean connect() {
		if (this.connected) return false;
        try {
            Class.forName(this.connector.getDriverPath());
            this.connection = DriverManager.getConnection(this.host, this.user, this.password);
            this.connected = true;
            return true;
        } catch (SQLException e) {
        	this.logger.log(Level.SEVERE, "Can't connect to the database", e);
        } catch (ClassNotFoundException e) {
        	this.logger.log(Level.SEVERE, "Can't find JDBC Driver", e);
        }
        return false;
	}

	@Override
	public boolean close() {
		if (!this.connected) return false;
        try {
        	this.connection.close();
            this.connected = false;
            return true;
        } catch (SQLException e) {
        	this.logger.log(Level.SEVERE, e.getMessage(), e);
        }
		return false;
	}

	@Override
	public boolean status() {
		return this.connected;
	}
	
	private String formaHost(String host){
		return "jdbc:" + this.connector.getName() + "://" + host + ":" + this.port + "/" + this.database;
	}
}
