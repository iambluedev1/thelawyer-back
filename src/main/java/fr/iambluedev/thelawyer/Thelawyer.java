package fr.iambluedev.thelawyer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.config.MysqlConfig;
import fr.iambluedev.thelawyer.config.WebConfig;
import fr.iambluedev.thelawyer.manager.ConfigManager;
import fr.iambluedev.thelawyer.manager.HotlinksManager;
import fr.iambluedev.thelawyer.manager.SanctionsManager;
import fr.iambluedev.thelawyer.mysql.connector.MariaDBConnector;
import fr.iambluedev.thelawyer.mysql.connector.MySQLConnector;
import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.task.RefreshTask;
import fr.iambluedev.thelawyer.web.Spark;
import lombok.Getter;

@Getter
public class Thelawyer {

	private Logger logger = Logger.getLogger("Thelawyer");
	private boolean started;
	private Database database;
	
	private HotlinksManager hotlinkManager;
	private SanctionsManager sanctionsManager;
	private ConfigManager configManager;
	
	private MysqlConfig mysqlConfig;
	private WebConfig webConfig;
	
	private boolean isRefreshing = false;
	private boolean rebuild = false;
	
	private ScheduledExecutorService scheduler;
	
	public Thelawyer(boolean rebuild) {
		logger.info("Starting App");
		
		this.rebuild = rebuild;
		
		this.configManager = new ConfigManager();
		
		try {
			this.mysqlConfig = this.configManager.loadMysqlConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.webConfig = this.configManager.loadWebConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.database = new Database(this.mysqlConfig.getHost(), this.mysqlConfig.getName(), mysqlConfig.getUser(), mysqlConfig.getPassword(), mysqlConfig.getPort(), ((mysqlConfig.getType().equals("mariadb")) ? new MariaDBConnector() : new MySQLConnector()));
		logger.info("Testing connection");
		this.database.connect();
		this.database.close();

		logger.info("Starting web server on localhost:" + this.webConfig.getPort());
		new Spark().setup(this.webConfig.getPort());
		
		logger.info("Init refresh task");
		this.scheduler = Executors.newScheduledThreadPool(1);
		this.scheduler.scheduleAtFixedRate(new RefreshTask(), 6, 12, TimeUnit.HOURS);
	}

	public Thelawyer start() {
		started = true;
		this.refresh();
		return this;
	}
	
	public void stop(){
		logger.info("Stopping app");
		this.database.close();
		
		logger.info("Stopping webserver");
		spark.Spark.stop();
		
		started = false;
	}
	
	public void refresh(){
		this.isRefreshing = true;
		
		logger.info("Starting Hotlinks' Manager");
		this.hotlinkManager = new HotlinksManager();
		this.hotlinkManager.fetch();
		this.hotlinkManager.refreshStats();
		
		logger.info("Starting Sanctions' Manager");
		this.sanctionsManager = new SanctionsManager();
		this.sanctionsManager.refreshSanctions(this.rebuild);
		this.sanctionsManager.populate();
		this.hotlinkManager.refreshStats();
		
		this.isRefreshing = false;
	}

}
