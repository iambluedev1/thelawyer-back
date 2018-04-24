package fr.iambluedev.thelawyer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sanctions {

	private List<Object> data;
	
	public Sanctions() {
		this.data = new ArrayList<Object>();
	}
	
}
