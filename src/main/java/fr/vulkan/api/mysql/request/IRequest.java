package fr.vulkan.api.mysql.request;

import java.util.List;

import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;

public interface IRequest {

	boolean executeUpdate(String query);
	
	Object select(String query, String get);
	
	List<IFields> select(String query);
	
	List<IField> select(String query, List<String> fields);
	
	List<Object> selectList(String query, String get);
	
	String getListOfWhereValues(List<IField> where);

	boolean exist(String query);

	List<IFields> selectList(String query);
	
}
