package fr.iambluedev.thelawyer.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import com.google.gson.Gson;

import fr.iambluedev.thelawyer.config.WebConfig;
import fr.iambluedev.thelawyer.config.MysqlConfig;

public class ConfigManager {

	private Logger logger = Logger.getLogger("ConfigManager");
	
	public MysqlConfig loadMysqlConfig() throws IOException{
		File file = new File("mysql.json");
		Gson gson = new Gson();
		if(file.exists()){
			logger.info("Loading mysql config file");
			return gson.fromJson(new FileReader(file), MysqlConfig.class);
		} else {
			logger.info("Saving mysql config file");
			MysqlConfig mysqlConfig = new MysqlConfig();
			mysqlConfig.setHost("localhost");
			mysqlConfig.setName("thelawyer");
			mysqlConfig.setPassword("");
			mysqlConfig.setType("mariadb");
			mysqlConfig.setUser("root");
			mysqlConfig.setPort(3306);
			
			try (Writer writer = new FileWriter(file)) {
			    gson.toJson(mysqlConfig, writer);
			}
			
			return mysqlConfig;
		}
	}
	
	public WebConfig loadWebConfig() throws IOException{
		File file = new File("web.json");
		Gson gson = new Gson();
		if(file.exists()){
			logger.info("Loading api config file");
			return gson.fromJson(new FileReader(file), WebConfig.class);
		} else {
			logger.info("Saving api config file");
			WebConfig webConfig = new WebConfig();
			webConfig.setPort(8080);
			webConfig.setCfscrape_url("http://127.0.0.1:8888");
			
			try (Writer writer = new FileWriter(file)) {
			    gson.toJson(webConfig, writer);
			}
			
			return webConfig;
		}
	}
	
}
