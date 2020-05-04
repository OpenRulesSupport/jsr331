package it.ssc.metadata.sql;

import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.exception.TypeSqlNotSupported;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateFieldsFromResultsetMetadata extends AbstractCreateFileds {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private ArrayList<FieldInterface> list_fields;
	
	public CreateFieldsFromResultsetMetadata(ResultSetMetaData rsmeta) throws SQLException, TypeSqlNotSupported {
		 int num_column=rsmeta.getColumnCount();
		 list_fields=new ArrayList<FieldInterface> ();
		 for(int a=1;a<=num_column;a++) {
			 String name=rsmeta.getColumnName(a).toUpperCase();
			 int size=rsmeta.getColumnDisplaySize(a);
			 String type_s_sql=rsmeta.getColumnTypeName(a);
			 int type_sql=rsmeta.getColumnType(a);
			 int precision=rsmeta.getPrecision(a);
			 int scale=rsmeta.getScale(a);
			
			 /*
			 logger.log(Level.FINE,"Campo name-> "+name);
			 logger.log(Level.FINE,"   Type name:"+type_s_sql );
			 logger.log(Level.FINE,"   Type sql (java.sql.Types):"+type_sql );
			 logger.log(Level.FINE,"   Size:"+size );
			 logger.log(Level.FINE,"   Precision:"+precision );
			 logger.log(Level.FINE,"   Scale:"+scale);
			 */
			
			 list_fields.add(createSingleField(name,size,type_sql,type_s_sql,precision,scale));
		 }	
	}
	
	public ArrayList<FieldInterface> getFieldsdCreated() {
		return list_fields;
	}
}
