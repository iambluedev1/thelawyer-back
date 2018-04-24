package fr.iambluedev.thelawyer.mysql.table;

import java.util.List;

import fr.iambluedev.thelawyer.mysql.database.Database;
import fr.iambluedev.thelawyer.mysql.field.Field;
import fr.iambluedev.thelawyer.mysql.request.RequestHandler;
import fr.iambluedev.thelawyer.mysql.util.Method;
import fr.vulkan.api.mysql.field.IField;
import fr.vulkan.api.mysql.field.IFields;
import fr.vulkan.api.mysql.table.ITable;
import lombok.Getter;

@Getter
public class Table implements ITable {

	private RequestHandler requestHandler;
    private Database database;
    private String name;
	private String quotesName;
	
	public Table(Database database, String name) {
        this.database = database;
        this.name = name;
        this.quotesName = " `" + name + "` ";
        this.requestHandler = new RequestHandler(database);
	}
	
	public Table(String name) {
        this.database = Database.getInstance();
        this.name = name;
        this.quotesName = " `" + name + "` ";
        this.requestHandler = new RequestHandler(database);
	}
	
	@Override
	public Object select(String field) {
		return requestHandler.select(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() + 
				quotesName, field);
	}
	
	@Override
	public List<IFields> selectAll() {
		return requestHandler.select(Method.SELECT.getName() + " * " + Method.FROM.getName() + 
				quotesName);
	}

	@Override
	public Object select(String field, IField where) {
		 return requestHandler.select(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() +
				 quotesName + Method.WHERE.getName() + where.getParsedNameAndValue(), field);
	}

	@Override
	public Object select(String field, List<IField> where) {
		return requestHandler.select(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + requestHandler.getListOfWhereValues(where), field);
	}

	@Override
	public List<Object> selectList(String field) {
		return requestHandler.selectList(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() + 
				quotesName, field);
	}

	@Override
	public List<Object> selectList(String field, IField where) {
		return requestHandler.selectList(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + where.getParsedNameAndValue(), field);
	}

	@Override
	public List<Object> selectList(String field, List<IField> where) {
		return requestHandler.selectList(Method.SELECT.getName() + " `" + field + "` " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + requestHandler.getListOfWhereValues(where), field);
	}

	@Override
	public boolean insert(List<IField> fields) {
		//Setup insert fields
        StringBuilder setter = new StringBuilder();
        StringBuilder field = new StringBuilder();
        for (IField entry : fields) {
            field.append(entry.getParsedName() + ", ");
            setter.append(entry.getParsedValue() + ", ");
        }

        //Update query
        return requestHandler.executeUpdate(Method.INSERT_INTO.getName() + quotesName + "(" +
                field.substring(0, field.length() - 2) + ") " + Method.VALUES.getName() +
            	"(" + setter.substring(0, setter.length() - 2) + ")");
	}

	@Override
	public boolean delete(IField where) {
		return requestHandler.executeUpdate(Method.DELETE_FROM.getName() + quotesName + Method.WHERE.getName() + 
				where.getParsedNameAndValue());
	}

	@Override
	public boolean update(IField toSet, IField where) {
		return requestHandler.executeUpdate(Method.UPDATE.getName() + quotesName + Method.SET.getName() +
				toSet.getParsedNameAndValue() + Method.WHERE.getName() + where.getParsedNameAndValue());
	}

	@Override
	public boolean update(List<IField> toSet, IField where) {
		//Setup updated fields
        StringBuilder setters = new StringBuilder();
        for (IField entry : toSet)
            setters.append(entry.getParsedNameAndValue() + ", ");

        //Update query
        return requestHandler.executeUpdate(Method.UPDATE.getName() + quotesName + Method.SET.getName() +
        		setters.substring(0, setters.length() - 2) + Method.WHERE.getName() + where.getParsedNameAndValue());
	}

	@Override
	public void dump() {
		int line = 1;
		for(IFields fields : this.selectAll()){
			System.out.print("Entry n°" + line + ": ");
			for(IField field : fields.getFields()) {
				Field formattedField = (Field) field;
				System.out.print(formattedField.getName() + "=" + formattedField.getParsedValue() + ", ");
			}
			System.out.println("");
			line++;
		}
	}

	@Override
	public List<IField> select(List<String> fields, IField where) {
		return requestHandler.select(Method.SELECT.getName() + " * " + Method.FROM.getName() + 
				quotesName + Method.WHERE.getName() + where.getParsedNameAndValue(), fields);
	}
	
	@Override
	public List<IField> select(List<String> fields, List<IField> where) {
		return requestHandler.select(Method.SELECT.getName() + " * " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + " " + requestHandler.getListOfWhereValues(where), fields);
	}

	@Override
	public boolean exist(IField field) {
		return requestHandler.exist(Method.SELECT.getName() + " " + field.getParsedName() + " " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + field.getParsedNameAndValue());
	}
	
	@Override
	public boolean exist(String field, List<IField> where) {
		return requestHandler.exist(Method.SELECT.getName() + " " + field + " " + Method.FROM.getName() +
				quotesName + Method.WHERE.getName() + requestHandler.getListOfWhereValues(where));
	}

	@Override
	public List<IFields> selectList(List<IField> where) {
		return requestHandler.selectList(Method.SELECT.getName() + " * " + Method.FROM.getName() + 
				quotesName + Method.WHERE.getName() + requestHandler.getListOfWhereValues(where));
	}
}
