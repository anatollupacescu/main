package my.nosql.datastore.model;

public class QueryCondition {

	public final String column;
	public final QueryOperator operator;
	public final String value;
	
	public QueryCondition(String column, QueryOperator operator, String value) {
		this.column = column;
		this.operator = operator;
		this.value = value;
	}
}
