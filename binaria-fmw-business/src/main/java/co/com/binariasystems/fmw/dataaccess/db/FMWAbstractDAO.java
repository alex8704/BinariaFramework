package co.com.binariasystems.fmw.dataaccess.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import co.com.binariasystems.fmw.constants.FMWConstants;
import co.com.binariasystems.fmw.dataaccess.FMWDAO;
import co.com.binariasystems.fmw.ioc.IOCHelper;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;

public class FMWAbstractDAO extends NamedParameterJdbcDaoSupport implements FMWDAO{
	protected PropertiesManager messages;
	protected String messagesFilePath;
	public Connection connect() throws Exception {
		return getConnection();
	}
	
	public void closeConn(Connection conn) {
		releaseConnection(conn);
	}

	public void closeAll(ResultSet rs, Statement stmt, Connection conn) {
		closeRs(rs);
        closeStmt(stmt);
        closeConn(conn);
	}

	public void closeRs(ResultSet rs) {
		try{
            if(rs != null)
                rs.close();
        }catch(Exception ex){}
        finally{rs = null;}
	}

	public void closeStmt(Statement stmt) {
		try{
            if(stmt != null)
                stmt.close();
        }catch(Exception ex){}
        finally{stmt = null;}
	}
	
	private boolean haveMessagesManager(){
		ensureMessageBundle();
		return messages != null;
	}
	
	protected void ensureMessageBundle(){
		if(messages != null) return;
		if(StringUtils.isEmpty(messagesFilePath)) return;
		Class loaderClass = IOCHelper.isConfigured() ? IOCHelper.getBean(FMWConstants.DEFAULT_LOADER_CLASS, Class.class) : this.getClass();
		this.messages = PropertiesManager.forPath(messagesFilePath, loaderClass);
	}
	
	protected String getString(String key){
    	if(!haveMessagesManager())
    		return key;
    	return messages.getString(key);
    }

	public PropertiesManager getMessages() {
		return messages;
	}

	public void setMessages(PropertiesManager messages) {
		this.messages = messages;
	}

	public String getMessagesFilePath() {
		return messagesFilePath;
	}

	public void setMessagesFilePath(String messagesFilePath) {
		this.messagesFilePath = messagesFilePath;
	}
	

}
