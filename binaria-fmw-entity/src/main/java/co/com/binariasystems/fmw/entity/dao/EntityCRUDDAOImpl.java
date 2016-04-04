package co.com.binariasystems.fmw.entity.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import co.com.binariasystems.fmw.dataaccess.db.FMWAbstractDAO;
import co.com.binariasystems.fmw.entity.resources.resources;
import co.com.binariasystems.fmw.exception.FMWDataAccessException;
import co.com.binariasystems.fmw.util.db.DBUtil;
import co.com.binariasystems.fmw.util.db.DBUtil.DBMS;
import co.com.binariasystems.fmw.util.exception.FMWExceptionUtils;
import co.com.binariasystems.fmw.util.messagebundle.PropertiesManager;
import co.com.binariasystems.fmw.util.pagination.ListPage;

public class EntityCRUDDAOImpl<T> extends FMWAbstractDAO implements EntityCRUDDAO<T> {
	
	public EntityCRUDDAOImpl() {
		messages = PropertiesManager.forPath(resources.getPropertyFilePath("entitycruddao.xml"), resources.class);
	}


	public void save(String sqlStatement, MapSqlParameterSource paramSource) throws FMWDataAccessException {
		try{
			getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
		}catch(DataAccessException ex){
			throw new FMWDataAccessException(FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
	}
	
	public void edit(String sqlStatement, MapSqlParameterSource paramSource)throws FMWDataAccessException {
		try{
			getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
		}catch(DataAccessException ex){
			throw new FMWDataAccessException(FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
	}
	
	public void delete(String sqlStatement, MapSqlParameterSource paramSource)throws FMWDataAccessException {
		try{
			getNamedParameterJdbcTemplate().update(sqlStatement, paramSource);
		}catch(DataAccessException ex){
			throw new FMWDataAccessException(FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
	}
	
	public ListPage<T> search(String sqlStatement, MapSqlParameterSource paramSource, int offset, int rowsByPage, RowMapper<T> rowMapper) throws FMWDataAccessException {
		return search(sqlStatement, paramSource, offset, rowsByPage, rowMapper, false);
	}

	private ListPage<T> search(String sqlStatement, MapSqlParameterSource paramSource, int offset, int rowsByPage, RowMapper<T> rowMapper, boolean ignorePagination) throws FMWDataAccessException {
		if(ignorePagination)
			return searchWithoutPaging(sqlStatement, paramSource, rowMapper);
		
		ListPage<T> resp = new ListPage<T>();
		try{
			int count = getNamedParameterJdbcTemplate().queryForObject(toCurrentDBMSCountQuery(sqlStatement, offset, rowsByPage), paramSource, Integer.class);
			List<T> data = null;
			if(count > 0)
				data = getNamedParameterJdbcTemplate().query(toCurrentDBMSPaginatedQuery(sqlStatement, (offset < 0 ? count -1 : offset), rowsByPage), paramSource, rowMapper);
			
			resp.setRowCount(count);
			resp.setData(data);
		}catch(DataAccessException ex){
			throw new FMWDataAccessException(FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
		return resp;
	}
	
	public ListPage<T> searchWithoutPaging(String sqlStatement, MapSqlParameterSource paramSource, RowMapper<T> rowMapper) throws FMWDataAccessException {
		ListPage<T> resp = new ListPage<T>();
		try{
			List<T> data = getNamedParameterJdbcTemplate().query(sqlStatement, paramSource, rowMapper);
			resp.setData(data);
			resp.setRowCount(data.size());
		}catch(DataAccessException ex){
			throw new FMWDataAccessException(FMWExceptionUtils.prettyMessageException(ex).getMessage());
		}
		return resp;
	}
	
	private String toCurrentDBMSPaginatedQuery(String queryStatemet, int offset, int rowsByPage){
		StringBuilder respBuilder = new StringBuilder();
		if(DBUtil.getCurrentDBMS() == DBMS.HSQLDB || DBUtil.getCurrentDBMS() == DBMS.MYSQL){
			respBuilder.append(queryStatemet).append(" limit ").append(offset).append(", ").append(rowsByPage);
		}
		if(DBUtil.getCurrentDBMS() == DBMS.ORACLE){
			respBuilder.append("select countQuery.* from (select rownum rowidx, indexQuery.* from (")
			.append(queryStatemet).append(") indexQuery) countQuery where countQuery.rowidx between ").append(offset + 1).append(" and ").append(offset + 1 + rowsByPage);
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
		return respBuilder.toString();
	}

}
