package co.com.binariasystems.fmw.exception;

public abstract class FMWAbstractRuntimeException extends RuntimeException {
	
	static {
		//Se carga de forma temprana la clase NextedExceptionUtils para evitar deadlock
		//en el classloader, al ejecutar sobre OSGI, al llamar el metodo getMessage()
		FMWAbstractRuntimeException.class.getName();
	}

	public FMWAbstractRuntimeException(String msg) {
		super(msg);
	}

	public FMWAbstractRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Override
	public String getMessage() {
		return RuntimeExceptionUtils.buildMessage(super.getMessage(), getCause());
	}


	public Throwable getRootCause() {
		Throwable rootCause = null;
		Throwable cause = getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return rootCause;
	}

	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof FMWAbstractRuntimeException) {
			return ((FMWAbstractRuntimeException) cause).contains(exType);
		}
		else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}

}
