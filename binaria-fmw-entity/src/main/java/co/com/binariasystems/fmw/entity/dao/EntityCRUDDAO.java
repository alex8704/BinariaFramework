package co.com.binariasystems.fmw.entity.dao;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import co.com.binariasystems.fmw.util.pagination.ListPage;

public interface EntityCRUDDAO<T> {
	public void save(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public void edit(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public void delete(String sqlStatement, MapSqlParameterSource paramSource) throws Exception;
	public ListPage<T> search(String sqlStatement, MapSqlParameterSource paramSource, int offset, int rowsByPage, RowMapper<T> rowMapper) throws Exception;
	public List<T> searchWithoutPaging(String sqlStatement, MapSqlParameterSource paramSource, RowMapper<T> rowMapper);
}
