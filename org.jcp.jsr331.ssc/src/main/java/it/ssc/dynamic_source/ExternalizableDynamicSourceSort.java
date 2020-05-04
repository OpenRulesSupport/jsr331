package it.ssc.dynamic_source;

import java.util.GregorianCalendar;

import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;

public abstract class ExternalizableDynamicSourceSort extends GenericDynamicSource {
	
	protected ExternalizableDynamicSourceSort(PDVAll pdv) {
		super(pdv);
	}
	
	
	protected String  writeExternal() {
		
		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String save_ext = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			pdv_field = pdv.getField(index_cicle_pdv);
			
			if(pdv_field.type==Integer.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0; } else  out.writeByte(1);     "+nl;
				save_ext+="out.writeInt("+pdv_field.getName()+");                "+nl;
			}
			else if(pdv_field.type==Double.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0.0D; } else  out.writeByte(1);  "+nl;
				save_ext+="out.writeDouble("+pdv_field.getName()+");             "+nl;
			}
			else if(pdv_field.type==String.class || pdv_field.type==StringBuffer.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=\"\"; } else out.writeByte(1);   "+nl;
				save_ext+="out.writeUTF("+pdv_field.getName()+");                "+nl;
			}
			else if(pdv_field.type==GregorianCalendar.class ) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+="out.writeLong(0);                                     "+nl;
				save_ext+=" } else  {  out.writeByte(1);                         "+nl;
				save_ext+="out.writeLong("+pdv_field.getName()+".getTimeInMillis());  }           "+nl;
			}
			else if(pdv_field.type==Float.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0.0F; } else  out.writeByte(1);  "+nl;
				save_ext+="out.writeFloat("+pdv_field.getName()+");              "+nl;
			}
			else if(pdv_field.type==Long.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0L; } else  out.writeByte(1);    "+nl;
				save_ext+="out.writeLong("+pdv_field.getName()+");               "+nl;
			}
			else if(pdv_field.type==Byte.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0; } else  out.writeByte(1);     "+nl;
				save_ext+="out.writeByte("+pdv_field.getName()+");               "+nl;
			}
			else if(pdv_field.type==Short.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"=0; } else  out.writeByte(1);     "+nl;
				save_ext+="out.writeShort("+pdv_field.getName()+");              "+nl;
			}
			else if(pdv_field.type==Character.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {               "+nl;
				save_ext+="out.writeByte(0);                                     "+nl;
				save_ext+=pdv_field.getName()+"='A'; } else  out.writeByte(1);   "+nl;
				save_ext+="out.writeChar("+pdv_field.getName()+");               "+nl;
			}
			else if(pdv_field.type==Boolean.class) {
				save_ext+="if("+pdv_field.getName()+"==null)     {                "+nl;
				save_ext+="out.writeByte(0);                                      "+nl;
				save_ext+=pdv_field.getName()+"=false; } else  out.writeByte(1);  "+nl;
				save_ext+="out.writeBoolean("+pdv_field.getName()+");             "+nl;
			}
		}
		return save_ext;
	}
	
	
	protected String  readExternal() {
		
		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String save_ext = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			pdv_field = pdv.getField(index_cicle_pdv);
			
			if(pdv_field.type==Integer.class) {
				save_ext+="if(in.readByte()==0)   {                            "+nl;
				save_ext+=pdv_field.getName()+"=null;                          "+nl;
				save_ext+="in.readInt();     } else                            "+nl;
				save_ext+=pdv_field.getName()+"=in.readInt();                  "+nl;
			}
			else if(pdv_field.type==Double.class) {
				save_ext+="if(in.readByte()==0)   {                            "+nl;
				save_ext+=pdv_field.getName()+"=null;                          "+nl;
				save_ext+="in.readDouble();     } else                         "+nl;
				save_ext+=pdv_field.getName()+"=in.readDouble();               "+nl;
			}
			else if(pdv_field.type==String.class || pdv_field.type==StringBuffer.class) {
				save_ext+="if(in.readByte()==0)   {                            "+nl;
				save_ext+=pdv_field.getName()+"=null;                          "+nl;
				save_ext+="in.readUTF();     } else                            "+nl;
				save_ext+=pdv_field.getName()+"=in.readUTF();                  "+nl;
			}
			
			else if(pdv_field.type==GregorianCalendar.class ) {
				save_ext+="if(in.readByte()==0)   {                             "+nl;
				save_ext+=pdv_field.getName()+"=null;                           "+nl;
				save_ext+="in.readLong();     } else   {                        "+nl;
				save_ext+=pdv_field.getName()+"= new java.util.GregorianCalendar();    "+nl;
				save_ext+=pdv_field.getName()+".setTimeInMillis(in.readLong());  }  "+nl;
			}
			else if(pdv_field.type==Float.class) {
				save_ext+="if(in.readByte()==0)   {                            "+nl;
				save_ext+=pdv_field.getName()+"=null;                          "+nl;
				save_ext+="in.readFloat();     } else                          "+nl;
				save_ext+=pdv_field.getName()+"=in.readFloat();                "+nl;
			}
			else if(pdv_field.type==Long.class) {
				save_ext+="if(in.readByte()==0)   {                            "+nl;
				save_ext+=pdv_field.getName()+"=null;                          "+nl;
				save_ext+="in.readLong();     } else                           "+nl;
				save_ext+=pdv_field.getName()+"=in.readLong();                 "+nl;
			}
			else if(pdv_field.type==Byte.class) {
				save_ext+="if(in.readByte()==0)   {                             "+nl;
				save_ext+=pdv_field.getName()+"=null;                           "+nl;
				save_ext+="in.readByte();     } else                            "+nl;
				save_ext+=pdv_field.getName()+"=in.readByte();                  "+nl;
			}
			else if(pdv_field.type==Character.class) {
				save_ext+="if(in.readByte()==0)   {                             "+nl;
				save_ext+=pdv_field.getName()+"=null;                           "+nl;
				save_ext+="in.readChar();     } else                            "+nl;
				save_ext+=pdv_field.getName()+"=in.readChar();                  "+nl;
			}
			else if(pdv_field.type==Boolean.class) {
				save_ext+="if(in.readByte()==0)   {                             "+nl;
				save_ext+=pdv_field.getName()+"=null;                           "+nl;
				save_ext+="in.readBoolean();     } else                         "+nl;
				save_ext+=pdv_field.getName()+"=in.readBoolean();               "+nl;
			}
		}
		return save_ext;
	}
}
