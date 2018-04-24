package fr.iambluedev.thelawyer.mysql.request;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.mysql.field.Field;
import fr.iambluedev.thelawyer.mysql.field.Fields;
import fr.iambluedev.thelawyer.mysql.util.Method;
import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;
import fr.vulkan.api.mysql.request.IRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestHandler implements IRequest {

	@Getter
	private String lastExecutedQuery;
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	@NonNull
	private Database database;
	
	@Override
	public boolean executeUpdate(String query) {
		database.connect();
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            sts.executeUpdate();
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
        database.close();
        return true;
	}

	@Override
	public Object select(String query, String get) {
		database.connect();
        Object request = null;
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            while (result.next())
                request = result.getObject(get);
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        database.close();
        return request;
	}
	
	@Override
	public List<IField> select(String query, List<String> fields) {
		database.connect();
		List<IField> datas = new ArrayList<IField>();
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            ResultSetMetaData rsmd = sts.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (result.next()) {
            	for (int i = 1; i <= columnCount; i++ ) {
            		String name = rsmd.getColumnName(i);
            		if(fields.contains(name)){
            			Field tmp = new Field(name, result.getObject(i));
            			datas.add(tmp);
            		}
             	}
            }
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        database.close();
        return datas;
	}
	
	@Override
	public List<IFields> select(String query) {
		database.connect();
		List<IFields> datas = new ArrayList<IFields>();
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            ResultSetMetaData rsmd = sts.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (result.next()) {
            	List<IField> list = new ArrayList<IField>();
            	for (int i = 1; i <= columnCount; i++ ) {
            		String name = rsmd.getColumnName(i);
                 	Field tmp = new Field(name, result.getObject(i));
                 	list.add(tmp);
             	}
            	datas.add(new Fields(list));
            }
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
        	logger.log(Level.SEVERE, e.getMessage(), e);
        }
        database.close();
        return datas;
	}

	@Override
	public List<Object> selectList(String query, String get) {
		database.connect();
        List<Object> request = new ArrayList<Object>();
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            while (result.next()) {
            	request.add(result.getObject(get));
            }
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        database.close();
        return request;
	}

	@Override
	public List<IFields> selectList(String query) {
		database.connect();
        List<IFields> request = new ArrayList<IFields>();
        try {
        	PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            ResultSetMetaData rsmd = sts.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (result.next()) {
            	List<IField> list = new ArrayList<IField>();
            	for (int i = 1; i <= columnCount; i++ ) {
            		String name = rsmd.getColumnName(i);
                 	Field tmp = new Field(name, result.getObject(i));
                 	list.add(tmp);
             	}
            	request.add(new Fields(list));
            }
            sts.close();
            this.lastExecutedQuery = query;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        database.close();
        return request;
	}
	
	@Override
	public String getListOfWhereValues(List<IField> where) {
		StringBuilder whereFields = new StringBuilder();
        where.forEach(whereField ->
        	whereFields.append(whereField.getParsedNameAndValue() + " " + Method.AND + " "));

        return whereFields.toString().substring(0, whereFields.length() - 5);
	}

	@Override
	public boolean exist(String query) {
		database.connect();
        boolean exist = false;
        try {
            PreparedStatement sts = database.getConnection().prepareStatement(query);
            ResultSet result = sts.executeQuery();
            
            sts.close();
            this.lastExecutedQuery = query;
            return result.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        database.close();
        return exist;
	}

}
