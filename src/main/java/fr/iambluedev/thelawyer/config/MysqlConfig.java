package fr.iambluedev.thelawyer.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MysqlConfig {

	private String type;
	private String host;
	private String user;
	private String password;
	private String name;
	private Integer port;
}
