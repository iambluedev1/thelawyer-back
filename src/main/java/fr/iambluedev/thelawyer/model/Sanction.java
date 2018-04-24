package fr.iambluedev.thelawyer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sanction {

	private Hotlink hotlink;
	private String banned_name;
	private String banned_by;
	private long ban_at;
	private Integer ban_type;
	private String ban_reason;
	
}
