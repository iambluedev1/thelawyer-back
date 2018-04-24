package fr.iambluedev.thelawyer.web;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.options;
import static spark.Spark.port;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.App;
import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.mysql.field.Field;
import fr.iambluedev.thelawyer.mysql.table.Table;
import fr.iambluedev.thelawyer.web.response.JsonResponse;
import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;

public class Spark {

	private Logger logger = Logger.getLogger("Spark");
	
	public void setup(Integer port) {
		port(port);
		
		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
            	response.header("Access-Control-Allow-Headers",	accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
		});

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
		
		get("/", (req, res) -> new JsonResponse(res)
			.add("name", "The Lawyer Webscraping Server")
			.add("author", "Iambluedev <iambluedev@gmx.fr>")
			.add("copyright", "vulkan-mc.fr")
			.add("version", "1.0.0")
			.get()
		);
		
		get("/refresh", (req, res) -> {
			if(App.getInstance().isRefreshing()) {
				return new JsonResponse(res)
						.add("status", "Refresh Task Already Started")
						.add("code", 0)
						.get();
			}else{
				new Thread(new Runnable() {
				    public void run() {
				    	App.getInstance().refresh();
				    }
				}).start();
				return new JsonResponse(res)
						.add("status", "Refresh Task Started")
						.add("code", 1)
						.get();
			}
			
		});
		
		get("/stats", (req, res) -> {
			int bans = App.getInstance().getHotlinkManager().getTotalBans();
			int mutes = App.getInstance().getHotlinkManager().getTotalMutes();
			int kicks = App.getInstance().getHotlinkManager().getTotalKicks();
			int warnings = App.getInstance().getHotlinkManager().getTotalWarnings();
			
			return new JsonResponse(res)
				.add("hotlinks", App.getInstance().getHotlinkManager().size())
				.add("bans", bans)
				.add("mutes", mutes)
				.add("kicks", kicks)
				.add("warnings", warnings)
				.add("sanctions", (bans+mutes+kicks+warnings))
				.get();
			}
		);
		
		get("/hotlinks", (req, res) -> new JsonResponse(res)
			.add("hotlinks", App.getInstance().getHotlinkManager().getHotLinks())
			.get()
		);
		
		get("/search/:name/:type/:server/:like", (req, res) -> {
			if(!this.isNumeric(req.params(":type")) ||  !this.isNumeric(req.params(":server")) || !this.isNumeric(req.params(":like"))){
				return new JsonResponse(res)
						.add("error", "bad params")
						.get();
			}
			
			String name = req.params(":name");
			Integer type = Integer.valueOf(req.params(":type"));
			Integer server = Integer.valueOf(req.params(":server"));
			
			
			if(server < 0 || server > App.getInstance().getHotlinkManager().size()){
				return new JsonResponse(res)
						.add("error", "bad params")
						.get();
			}

			if(type < 0 || type > 4){
				return new JsonResponse(res)
						.add("error", "bad params")
						.get();
			}
			
			Integer tmpLike = Integer.valueOf(req.params(":like"));
			
			if(tmpLike < 0 || tmpLike > 1){
				return new JsonResponse(res)
						.add("error", "bad params")
						.get();
			}
			
			boolean like = (tmpLike == 1) ? true : false;
			
			logger.info("Search Request for name=[" + name + "], type=[" + this.formatType(type) + "], server=[" + ((server == 0) ? "all" : App.getInstance().getHotlinkManager().getHotLinks().get(server - 1).getName()) + "], like=[" + like + "]");
			
			if(type != 0){
				List<IFields> datas = new ArrayList<IFields>();
				datas = this.existAndSelect(name, type, server, like);
				if(datas != null){
					Map<String, Object> t = new HashMap<String, Object>();
					t.put(this.formatType(type), this.format(datas));
					return new JsonResponse(res)
							.add("result", t)
							.get();
				}else{
					return new JsonResponse(res)
							.add("result", "")
							.get();
				}
			} else {
				List<IFields> bansDatas = this.existAndSelect(name, 1, server, like);
				List<IFields> mutesDatas = this.existAndSelect(name, 2, server, like);
				List<IFields> kicksDatas = this.existAndSelect(name, 3, server, like);
				List<IFields> warningsDatas = this.existAndSelect(name, 4, server, like);
				
				Map<String, Object> t = new HashMap<String, Object>();
				t.put("bans", this.format(bansDatas));
				t.put("mutes", this.format(mutesDatas));
				t.put("kicks", this.format(kicksDatas));
				t.put("warnings", this.format(warningsDatas));
				
				return new JsonResponse(res)
						.add("result", t)
						.get();
			}
		});
		
		get("/search/:name", (req, res) -> {
			String name = req.params(":name");
			
			List<Object> tmp = new ArrayList<Object>();
			tmp.addAll(this.searchPlayer(name, 1));
			tmp.addAll(this.searchPlayer(name, 2));
			tmp.addAll(this.searchPlayer(name, 3));
			tmp.addAll(this.searchPlayer(name, 4));
			
			Set<Object> names = new HashSet<Object>();
			names.addAll(tmp);
			
			return new JsonResponse(res)
					.add("result", names)
					.get();
		});
		
		notFound((req, res) -> new JsonResponse(res)
			.add("error", "404 - Not Found")
			.get()
		);
	}
	
	private boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	private List<Map<String, Object>> format(List<IFields> datas){
		List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
		for(IFields iFields : datas){
			Map<String, Object> fields = new HashMap<String, Object>();
			for(IField field : iFields.getFields()){
				Field el = (Field) field;
				String[] name = el.getName().split("_");
				if(name[1].equals("id")){
					fields.put("hotlink_id", el.getValue());
				}else{
					fields.put("sanction_" + name[1], el.getValue());
				}
			}
			tmp.add(fields);
		}
		return tmp;
	}
	
	private String formatType(Integer type){
		return (type == 1) ? "bans" : (type == 2) ? "mutes" : (type == 3) ? "kicks" : (type == 4) ? "warnings" : "bans";
	}
	
	private String formatTableKey(Integer type){
		return (type == 1) ? "ban" : (type == 2) ? "mute" : (type == 3) ? "kick" : (type == 4) ? "warn" : "bans";
	}
	
	private List<IFields> existAndSelect(String name, Integer t, Integer server, boolean like){
		String type = this.formatType(t);
		String typeTable = this.formatTableKey(t);
		String typeTable2 = this.formatTableKey(t) + ((t == 2) ? "d" : (t == 1) ? "ned" : "ed");
		Table table = (Table) Database.getInstance().getTable(type);
		List<IFields> datas = new ArrayList<IFields>();
		
		if(server == 0){
			if(table.exist(new Field(typeTable2 + "_name", name, true))){
				datas = table.selectList(Arrays.asList(new Field(typeTable2 + "_name", name, like)));
			}
		} else {
			if(table.exist(typeTable + "_id", Arrays.asList(new Field(typeTable2 +"_name", name, like), new Field("hotlink_id", server)))){
				datas = table.selectList(Arrays.asList(new Field(typeTable2 +"_name", name, like), new Field("hotlink_id", server)));
			}
		}
		
		return datas;
	}
	
	private List<Object> searchPlayer(String player, Integer t){
		String type = this.formatType(t);
		String typeTable = this.formatTableKey(t) + ((t == 2) ? "d" : (t == 1) ? "ned" : "ed");
		Table table = (Table) Database.getInstance().getTable(type);
		return table.selectList(typeTable + "_name", new Field(typeTable + "_name", player, true));
	}
}
