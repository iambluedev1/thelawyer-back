package fr.iambluedev.thelawyer.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.App;
import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.mysql.field.Field;
import fr.iambluedev.thelawyer.mysql.table.Table;
import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;
import lombok.Getter;

@Getter
public class HotlinksManager {

	private Logger logger = Logger.getLogger("HotLinkManager");
	
	private List<Hotlink> hotLinks;
	
	public HotlinksManager() {
		this.hotLinks = new ArrayList<Hotlink>();
	}
	
	public Integer size(){
		return this.hotLinks.size();
	}
	
	public void add(Hotlink hotlink){
		if(!this.exist(hotlink))
			this.hotLinks.add(hotlink);
	}
	
	public boolean exist(Hotlink hotlink){
		return this.hotLinks.contains(hotlink);
	}
	
	public Integer getTotalBans(){
		int count = 0;
		for(Hotlink hot : this.hotLinks)
			count += hot.getBanCount();
		return count;
	}
	
	public Integer getTotalMutes(){
		int count = 0;
		for(Hotlink hot : this.hotLinks)
			count += hot.getMuteCount();
		return count;
	}
	
	public Integer getTotalWarnings(){
		int count = 0;
		for(Hotlink hot : this.hotLinks)
			count += hot.getWarningCount();
		return count;
	}
	
	public Integer getTotalKicks(){
		int count = 0;
		for(Hotlink hot : this.hotLinks)
			count += hot.getKickCount();
		return count;
	}
	
	public void refreshStats(){
		logger.info("Refreshing stats");
		for(Hotlink hot : this.hotLinks){
			Table bans = (Table) Database.getInstance().getTable("bans");
			Table mutes = (Table) Database.getInstance().getTable("mutes");
			Table kicks = (Table) Database.getInstance().getTable("kicks");
			Table warnings = (Table) Database.getInstance().getTable("warnings");
			
			int bansCount = bans.selectList("ban_id", new Field("hotlink_id", hot.getId())).size();
			int mutesCount = mutes.selectList("mute_id", new Field("hotlink_id", hot.getId())).size();
			int kicksCount = kicks.selectList("kick_id", new Field("hotlink_id", hot.getId())).size();
			int warningsCount = warnings.selectList("warn_id", new Field("hotlink_id", hot.getId())).size();
			
			logger.info("[" + hot.getName() + ":] => " + bansCount + " bans, " + mutesCount + " mutes, " + kicksCount + " kicks and " + warningsCount + " warnings");
			hot.setBanCount(bansCount);
			hot.setMuteCount(mutesCount);
			hot.setWarningCount(warningsCount);
			hot.setKickCount(kicksCount);
		}
		
		int bans = App.getInstance().getHotlinkManager().getTotalBans();
		int mutes = App.getInstance().getHotlinkManager().getTotalMutes();
		int kicks = App.getInstance().getHotlinkManager().getTotalKicks();
		int warnings = App.getInstance().getHotlinkManager().getTotalWarnings();
		
		logger.info("Total of " + bans + " bans, " + mutes + " mutes, " + kicks + " kicks and " + warnings + " warnings");
	}
	
	public void fetch(){
		logger.info("Starting refresh hotlinks' cache");
		Database bdd = Database.getInstance();
		Table hotlinks = (Table) bdd.getTable("hotlinks");
		for(IFields fields : hotlinks.selectAll()){
			Hotlink tmp = new Hotlink();
			for(IField field : fields.getFields()) {
				Field formattedField = (Field) field;
				
				if(formattedField.getName().equals("hotlink_id")){
					tmp.setId((Integer) formattedField.getValue());
				}
				
				if(formattedField.getName().equals("hotlink_name")){
					tmp.setName((String) formattedField.getValue());
				}
				
				if(formattedField.getName().equals("hotlink_type")){
					tmp.setType((String) formattedField.getValue());
				}
				
				if(formattedField.getName().equals("hotlink_banlist_url")){
					tmp.setBanlist((String) formattedField.getValue());
				}
				
				if(formattedField.getName().equals("hotlink_main_url")){
					tmp.setWebsite((String) formattedField.getValue());
				}
			}
			logger.info("Added " + tmp.getName() + " to cache");
			this.add(tmp);
		}
		
		logger.info("There are now " + this.size() + " hotlink up !");
	}
}
