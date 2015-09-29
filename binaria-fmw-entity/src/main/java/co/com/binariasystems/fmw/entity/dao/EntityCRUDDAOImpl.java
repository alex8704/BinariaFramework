package co.com.binariasystems.fmw.entity.dao;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import co.com.binariasystems.fmw.dataaccess.db.FMWAbstractDAO;
import co.com.binariasystems.fmw.util.db.DBUtil;
import co.com.binariasystems.fmw.util.db.DBUtil.DBMS;
import co.com.binariasystems.fmw.util.pagination.ListPage;

public class EntityCRUDDAOImpl extends FMWAbstractDAO implements EntityCRUDDAO {


	public void save(String sqlStatement, MapSqlParameterSource paramSource) throws Exception {
		getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
		
	}
	
	public void edit(String sqlStatement, MapSqlParameterSource paramSource)throws Exception {
		getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
	}

	public ListPage<Object> search(String sqlStatement, MapSqlParameterSource paramSource, int offset, int rowsByPage, RowMapper<Object> rowMapper) throws Exception {
		ListPage<Object> resp = new ListPage<Object>();
		int count = getNamedParameterJdbcTemplate().queryForObject(toCurrentDBMSCountQuery(sqlStatement, offset, rowsByPage), paramSource, Integer.class);
		List<Object> data = null;
		if(count > 0)
			data = getNamedParameterJdbcTemplate().query(toCurrentDBMSPaginatedQuery(sqlStatement, (offset < 0 ? count -1 : offset), rowsByPage), paramSource, rowMapper);
		
		resp.setRowCount(count);
		resp.setData(data);
		return resp;
	}
	
	public void delete(String sqlStatement, MapSqlParameterSource paramSource)throws Exception {
		getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
	}
	
	private String toCurrentDBMSPaginatedQuery(String queryStatemet, int offset, int rowsByPage){
		StringBuilder respBuilder = new StringBuilder();
		if(DBUtil.getCurrentDBMS() == DBMS.HSQLDB || DBUtil.getCurrentDBMS() == DBMS.MYSQL){
			respBuilder.append(queryStatemet).append(" limit ").append(offset).append(", ").append(rowsByPage);
		}
		if(DBUtil.getCurrentDBMS() == DBMS.ORACLE){
			respBuilder.append("select alias2.* from (select rownum rowidx, alias1.* from (")
			.append(queryStatemet).append(") alias1) alias2 where alias2.rowidx between ").append(offset + 1).append(" and ").append(offset + 1 + rowsByPage);
		}
		if(DBUtil.getCurrentDBMS() == DBMS.POSTGRES){
			respBuilder.append(queryStatemet).append(" offset ").append(offset).append(" limit ").append(rowsByPage);
		}if(DBUtil.getCurrentDBMS() == DBMS.SQLSERVER){
			respBuilder.append(queryStatemet).append(" offset ").append(offset).append(" rows fetch next ").append(rowsByPage).append(" rows only");
		}
		
		return respBuilder.toString();
	}
	
	private String toCurrentDBMSCountQuery(String queryStatemet, int offset, int rowsByPage){
		StringBuilder respBuilder = new StringBuilder();
		int fromIdx = queryStatemet.indexOf(" from ");
		respBuilder.append("select count(*)").append(queryStatemet.substring(fromIdx, queryStatemet.indexOf(" order by ")));
		//respBuilder.append("select count(*) from (").append(queryStatemet).append(")");
		return respBuilder.toString();
	}

	public List<Object> searchWithoutPaging(String sqlStatement, MapSqlParameterSource paramSource, RowMapper<Object> rowMapper) {
		List<Object> data = getNamedParameterJdbcTemplate().query(sqlStatement, paramSource, rowMapper);
		return data;
	}

}
