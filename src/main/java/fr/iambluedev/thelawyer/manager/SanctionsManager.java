package fr.iambluedev.thelawyer.manager;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.App;
import fr.iambluedev.thelawyer.fetcher.JsonFetcher;
import fr.iambluedev.thelawyer.fetcher.LiteBansFetcher;
import fr.iambluedev.thelawyer.fetcher.LiteBansForkFetcher;
import fr.iambluedev.thelawyer.fetcher.PersonalFetcher;
import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.model.Sanction;
import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.mysql.field.Field;
import fr.iambluedev.thelawyer.mysql.table.Table;
import fr.vulkan.api.mysql.field.IField;
import lombok.Getter;

@Getter
public class SanctionsManager {

	private Logger logger = Logger.getLogger("SanctionsManager");
	private List<Sanction> sanctions;
	private List<Sanction> toSave;
	
	public SanctionsManager() {
		this.sanctions = new ArrayList<Sanction>();
		this.toSave = new ArrayList<Sanction>();
	}
	
	public Integer size(){
		return this.sanctions.size();
	}
	
	public void add(Sanction sanction){
		if(!this.exist(sanction))
			this.sanctions.add(sanction);
	}
	
	public boolean exist(Sanction sanction){
		return this.sanctions.contains(sanction);
	}
	
	public void fetch(Hotlink hotlink, boolean rebuild) throws IOException, ParseException{
		logger.info("Fetching sanctions for hotlink " + hotlink.getName());
		if(hotlink.getType().equals("json")){
			this.sanctions.addAll(new JsonFetcher().fetch(hotlink, rebuild));
		}
		
		if(hotlink.getType().equals("litebans")){
			this.sanctions.addAll(new LiteBansFetcher().fetch(hotlink, rebuild));
		}
		
		if(hotlink.getType().equals("fork_litebans")){
			this.sanctions.addAll(new LiteBansForkFetcher().fetch(hotlink, rebuild));
		}
	
		if(hotlink.getType().equals("personal")){
			this.sanctions.addAll(new PersonalFetcher().fetch(hotlink, rebuild));
		}
	}
	
	public void refreshSanctions(boolean rebuild){
		logger.info("Refreshing sanctions");
		if(rebuild) {
			logger.info("Rebuild mode activated !");
		}
		for(Hotlink hot : App.getInstance().getHotlinkManager().getHotLinks()){
			try {
				this.fetch(hot, rebuild);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		logger.info(this.sanctions.size() + " sanctions registered !");
		logger.info("Task finished");
	}
	
	//TODO : A refactoriser
	public void populate(){
		logger.info("Now, it's time to update the database !");
		if(this.sanctions.size() > 0 ) {
			logger.info("Verifying new sanctions, may take a while");
			
			Table bans = (Table) Database.getInstance().getTable("bans");
			Table mutes = (Table) Database.getInstance().getTable("mutes");
			Table kicks = (Table) Database.getInstance().getTable("kicks");
			Table warnings = (Table) Database.getInstance().getTable("warnings");
			
			int i = 1;
			for(Sanction sanction : this.sanctions){
				List<IField> banFields =  Arrays.asList(
						new Field("hotlink_id", sanction.getHotlink().getId()),
						new Field("ban_at", sanction.getBan_at()),
						new Field("banned_name", sanction.getBanned_name())
				);
				
				List<IField> muteFields =  Arrays.asList(
						new Field("hotlink_id", sanction.getHotlink().getId()),
						new Field("mute_at", sanction.getBan_at()),
						new Field("muted_name", sanction.getBanned_name())
				);
				
				List<IField> kicksFields =  Arrays.asList(
						new Field("hotlink_id", sanction.getHotlink().getId()),
						new Field("kick_at", sanction.getBan_at()),
						new Field("kicked_name", sanction.getBanned_name())
				);
				
				List<IField> warningsFields =  Arrays.asList(
						new Field("hotlink_id", sanction.getHotlink().getId()),
						new Field("warn_at", sanction.getBan_at()),
						new Field("warned_name", sanction.getBanned_name())
				);
				
				if(sanction.getBan_type() == 1) {
					if(!mutes.exist("mute_id", muteFields)){
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [mute] no saved");
						this.toSave.add(sanction);
					}else{
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [mute] saved");
					}
				} else if(sanction.getBan_type() == 2){
					if(!bans.exist("ban_id", banFields)){
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [ban] no saved");
						this.toSave.add(sanction);
					}else{
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [ban] no saved");
					}
				} else if(sanction.getBan_type() == 3){
					if(!warnings.exist("warn_id", warningsFields)){
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [warning] no saved");
						this.toSave.add(sanction);
					}else{
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [warning] no saved");
					}
				} else if(sanction.getBan_type() == 4){
					if(!kicks.exist("kick_id", kicksFields)){
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [kick] no saved");
						this.toSave.add(sanction);
					}else{
						logger.info("[" + i + "/" + this.sanctions.size() + "] Entry for : " + sanction.getBanned_name() + " [" + sanction.getBan_reason() + "] [kick] no saved");
					}
				}
				i++;
			}
			
			logger.info(this.toSave.size() + " of " + this.sanctions.size() + " sanctions are marked to be saved");
			
			i = 1;
			for(Sanction save : toSave){
				if(save.getBan_type() == 1) {
					logger.info("[" + i +"/" + toSave.size() + "] [mute] Saving entry for : " + save.getBanned_name() + " [" + save.getBan_reason() + "]");
					mutes.insert(Arrays.asList(
						new Field("hotlink_id", save.getHotlink().getId()),
						new Field("muted_name", save.getBanned_name()),
						new Field("mute_reason", save.getBan_reason()),
						new Field("muted_by", save.getBanned_by()),
						new Field("mute_at", save.getBan_at())
					));
				} else if(save.getBan_type() == 2){
					logger.info("[" + i +"/" + toSave.size() + "] [ban] Saving entry for : " + save.getBanned_name() + " [" + save.getBan_reason() + "]");
					bans.insert(Arrays.asList(
						new Field("hotlink_id", save.getHotlink().getId()),
						new Field("banned_name", save.getBanned_name()),
						new Field("ban_reason", save.getBan_reason()),
						new Field("banned_by", save.getBanned_by()),
						new Field("ban_at", save.getBan_at())
					));
				} else if(save.getBan_type() == 3){
					logger.info("[" + i +"/" + toSave.size() + "] [warning] Saving entry for : " + save.getBanned_name() + " [" + save.getBan_reason() + "]");
					warnings.insert(Arrays.asList(
						new Field("hotlink_id", save.getHotlink().getId()),
						new Field("warned_name", save.getBanned_name()),
						new Field("warn_reason", save.getBan_reason()),
						new Field("warned_by", save.getBanned_by()),
						new Field("warn_at", save.getBan_at())
					));
				} else if(save.getBan_type() == 4){
					logger.info("[" + i +"/" + toSave.size() + "] [kick] Saving entry for : " + save.getBanned_name() + " [" + save.getBan_reason() + "]");
					kicks.insert(Arrays.asList(
						new Field("hotlink_id", save.getHotlink().getId()),
						new Field("kicked_name", save.getBanned_name()),
						new Field("kick_reason", save.getBan_reason()),
						new Field("kicked_by", save.getBanned_by()),
						new Field("kick_at", save.getBan_at())
					));
				}
				i++;
			}
			
			logger.info("Clearing registered sanctions");
			this.sanctions.clear();
			this.toSave.clear();
			logger.info("The database is up to date");
		}
	}
}
