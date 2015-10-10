package co.com.binariasystems.fmw.dataaccess;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface JPABasedDAO<T, ID extends Serializable> extends FMWDAO, JpaSpecificationExecutor<T>, PagingAndSortingRepository<T, Serializable> {
}
