package co.com.binariasystems.fmw.util.exception;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.CleanupFailureDataAccessException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.dao.UncategorizedDataAccessException;

import co.com.binariasystems.fmw.exception.FMWDataAccessException;
import co.com.binariasystems.fmw.exception.FMWException;
import co.com.binariasystems.fmw.exception.FMWUncheckedException;
import co.com.binariasystems.fmw.util.FMWUtilConstants;
import co.com.binariasystems.fmw.util.messagebundle.MessageBundleManager;

public class FMWExceptionUtils {
	private static Map<Class<? extends Exception>, String> springExceptionMsgMapping = new HashMap<Class<? extends Exception>, String>();
	private static MessageBundleManager mm = MessageBundleManager.forPath(FMWUtilConstants.BFMW_RESOURCES_PACKAGE+"."+FMWUtilConstants.BFMW_DAOEXCEPTIONS_MESSAGESFILE);
	private static final String DEFAUL_DATAACCESERROR_MSG_KEY = DataAccessException.class.getSimpleName().toLowerCase()+".error_message";
	
	static{
		springExceptionMsgMapping.put(EmptyResultDataAccessException.class, EmptyResultDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(IncorrectUpdateSemanticsDataAccessException.class, IncorrectUpdateSemanticsDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(NonTransientDataAccessException.class, NonTransientDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(PessimisticLockingFailureException.class, PessimisticLockingFailureException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(IncorrectResultSizeDataAccessException.class, IncorrectResultSizeDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(CannotAcquireLockException.class, CannotAcquireLockException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(TransientDataAccessResourceException.class, TransientDataAccessResourceException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DataAccessResourceFailureException.class, DataAccessResourceFailureException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(UncategorizedDataAccessException.class, UncategorizedDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(CannotSerializeTransactionException.class, CannotSerializeTransactionException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(PermissionDeniedDataAccessException.class, PermissionDeniedDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(RecoverableDataAccessException.class, RecoverableDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DataRetrievalFailureException.class, DataRetrievalFailureException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DataIntegrityViolationException.class, DataIntegrityViolationException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DeadlockLoserDataAccessException.class, DeadlockLoserDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(InvalidDataAccessResourceUsageException.class, InvalidDataAccessResourceUsageException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(InvalidDataAccessApiUsageException.class, InvalidDataAccessApiUsageException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(CleanupFailureDataAccessException.class, CleanupFailureDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(QueryTimeoutException.class, QueryTimeoutException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(OptimisticLockingFailureException.class, OptimisticLockingFailureException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(NonTransientDataAccessResourceException.class, NonTransientDataAccessResourceException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(TypeMismatchDataAccessException.class, TypeMismatchDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(ConcurrencyFailureException.class, ConcurrencyFailureException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DuplicateKeyException.class, DuplicateKeyException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(TransientDataAccessException.class, TransientDataAccessException.class.getSimpleName().toLowerCase()+".error_message");
		springExceptionMsgMapping.put(DataAccessException.class, DataAccessException.class.getSimpleName().toLowerCase()+".error_message");
	}
	
	public static Throwable prettyMessageException(Throwable ex){
		if(ex instanceof FMWException || ex instanceof FMWUncheckedException || ex instanceof FMWDataAccessException)
			return ex;			
		if(ex instanceof SQLException){
			String msgKey = "sql.state.code."+((SQLException)ex).getSQLState();
			boolean foundMessage = !StringUtils.defaultIfEmpty(mm.getString(msgKey), msgKey).equals(msgKey);
			if(foundMessage)
				return new FMWDataAccessException(mm.getString(msgKey));
		}else if(ex instanceof DataAccessException){
			String msgKey = StringUtils.defaultIfEmpty(springExceptionMsgMapping.get(ex.getClass()), DEFAUL_DATAACCESERROR_MSG_KEY);
			return new FMWDataAccessException(mm.getString(msgKey));
		}else if(ex instanceof RuntimeException || ex instanceof InvocationTargetException){
			Throwable tr = ex;
			while(tr.getCause() != null){
				tr = tr.getCause();
			}
			return new FMWException(tr instanceof NullPointerException ? "Null Value" : ex.getMessage(), tr);
		}
		return ex;
	}
}
