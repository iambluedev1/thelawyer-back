package fr.iambluedev.thelawyer.mysql.util;

public enum Method {

    SELECT, WHERE, FROM, INSERT_INTO, VALUES, UPDATE, DELETE_FROM, SET, AND;
	
    public String getName() {
        return name().replace("_", " ");
    }

}