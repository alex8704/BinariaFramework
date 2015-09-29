package co.com.binariasystems.fmw.entity.dao;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import co.com.binariasystems.fmw.util.pagination.ListPage;

public interface EntityCRUDDAO {
	public void save(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public void edit(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public void delete(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public ListPage<Object> search(String sqlStatement, MapSqlParameterSource paramSource, int offset, int rowsByPage, RowMapper<Object> rowMapper) throws Exception;
	public List<Object> searchWithoutPaging(String sqlStatement, MapSqlParameterSource paramSource, RowMapper<Object> rowMapper);
}
