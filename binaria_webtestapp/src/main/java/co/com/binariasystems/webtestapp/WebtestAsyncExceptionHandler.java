package co.com.binariasystems.webtestapp;

import static co.com.binariasystems.fmw.constants.FMWConstants.LINE_SEP;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import co.com.binariasystems.fmw.reflec.TypeHelper;

public class WebtestAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	Logger LOGGER = LoggerFactory.getLogger(WebtestAsyncExceptionHandler.class);
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Ha ocurrido un error invocando metodo asincrono").append(LINE_SEP)
		.append("Metodo: ").append(method.getName()).append(LINE_SEP)
		.append("Parametros: {");
		for(int i = 0; i < params.length; i++){
			Object param = params[i];
			msgBuilder.append(i == 0 ? "" : ", ").append("'").append(TypeHelper.objectToString(param)).append("'");
		}
		msgBuilder.append("}").append(LINE_SEP)
		.append("Mensaje de Error: ").append("'").append(ex.getMessage()).append("'").append(LINE_SEP);
		LOGGER.error(msgBuilder.toString(), ex);
	}

}
