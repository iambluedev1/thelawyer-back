package fr.iambluedev.thelawyer.mysql.field;

import fr.vulkan.api.mysql.field.IField;
import lombok.Getter;

@Getter
public class Field implements IField {

	private String name;
	private Object value;
	private boolean like = false;
	
	public Field(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public Field(String name, Object value, boolean like) {
		this.name = name;
		this.value = value;
		this.like = like;
	}
	
	@Override
	public String getParsedName() {
		return "`" + name + "`";
	}

	@Override
	public String getParsedValue() {
		String formatted = String.valueOf(value);
		formatted = formatted.replaceAll("'", "\\\\'").replaceAll("\\P{Print}", "");
		if(like) {
			return "'%" + formatted + "%'";
		}
		return "'" + formatted + "'";
	}

	@Override
	public String getParsedNameAndValue() {
		return getParsedName() + ((this.like) ? " LIKE " : "=") + getParsedValue();
	}

}
