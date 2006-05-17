package org.dianexus.triceps.modules.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class MysqlInstrumentDAO implements InstrumentDAO{
	
	private static final String SQL_GET_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

	private static final String SQL_INSTRUMENT_VERSION_NEW = "INSERT INTO instrument_version SET instrument_id = ? , "
			+ " instance_table_name= ? , instrument_notes = ? , instrument_status = ? ";


	private static final String SQL_INSTRUMENT_VERSION_DELETE = "DELETE FROM instrument_version WHERE instrument_version_id = ?";

	private static final String SQL_INSTRUMENT_VERSION_UPDATE = "UPDATE instrument_version SET instrument_id = ? , "
			+ " instance_table_name= ? , instrument_notes = ? , instrument_status = ? ";

	private static final String SQL_INSTRUMENT_ID_GET = "SELECT * FROM instrument WHERE instrument_id = ?";
	private static final String SQL_INSTRUMENT_NAME_GET = "SELECT * FROM instrument WHERE  instrument_name = ?";
	
	private String instrumentDescription;
	private int instrumentId;
	private String instrumentName;
	
	
	public boolean deleteInstrument(int _id) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean updateInstrument(String _column, String value) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean getInstrument(int _id) {


		Connection con = DialogixMysqlDAOFactory.createConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;
		try {
			ps = con.prepareStatement(SQL_INSTRUMENT_ID_GET);
			ps.clearParameters();
			ps.setInt(1, _id);
			
			rs = ps.executeQuery();
			
			
			if(rs.next()){
				this.instrumentId = _id;
				this.instrumentName =  rs.getString(2);
				this.instrumentDescription = rs.getString(3);
				
			}
			

		} catch (Exception e) {

			e.printStackTrace();
			return false;

		} finally {
			try {
				if(rs != null){
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception fe) {
				fe.printStackTrace();
			}
		}
		return true;
	}

	public boolean getInstrument(String _name) {
		Connection con = DialogixMysqlDAOFactory.createConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsm = null;
		try {
			ps = con.prepareStatement(SQL_INSTRUMENT_NAME_GET);
			ps.clearParameters();
			ps.setString(1, _name);
			
			rs = ps.executeQuery();
			
			
			if(rs.next()){
				this.instrumentId =  rs.getInt(1);
				this.instrumentName = _name;
				this.instrumentDescription = rs.getString(3);
				
			}
			

		} catch (Exception e) {

			e.printStackTrace();
			return false;

		} finally {
			try {
				if(rs != null){
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception fe) {
				fe.printStackTrace();
			}
		}
		return true;
	}
	public boolean setInstrument() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getInstrumentDescription() {
		
		return this.instrumentDescription;
	}

	public int getInstrumentId() {
		
		return this.instrumentId;
	}

	public int getInstrumentLastInsertId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getInstrumentName() {
		
		return this.instrumentName;
	}



	public void setInstrumentDescription(String instrument_description) {
		this.instrumentDescription = instrument_description;
		
	}

	public void setInstrumentId(int _id) {
		this.instrumentId=_id;
		
	}

	public void setInstrumentName(String _instrumentName) {
		this.instrumentName=_instrumentName;
		
	}

	
	
	

}