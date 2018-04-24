package fr.iambluedev.thelawyer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hotlink {

	private Integer id;
	private String name;
	private String type;
	private String banlist;
	private String website;
	
	private Integer banCount = 0;
	private Integer muteCount = 0;
	private Integer warningCount = 0;
	private Integer kickCount = 0;
	
	public void addBan(){
		this.banCount++;
	}
	
	public void addMute(){
		this.muteCount++;
	}
	
	public void addWarning(){
		this.warningCount++;
	}
	
	public void addKick(){
		this.kickCount++;
	}
}
