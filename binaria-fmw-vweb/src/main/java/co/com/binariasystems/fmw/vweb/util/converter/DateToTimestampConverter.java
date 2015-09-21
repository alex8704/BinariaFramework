package co.com.binariasystems.fmw.vweb.util.converter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class DateToTimestampConverter implements Converter<Date, Timestamp> {

	@Override
	public Timestamp convertToModel(Date value, Class<? extends Timestamp> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (targetType != getModelType()) {
            throw new ConversionException("Converter only supports "
                    + getModelType().getName() + " (targetType was "
                    + targetType.getName() + ")");
        }

        if (value == null) {
            return null;
        }

        return new Timestamp(value.getTime());
	}

	@Override
	public Date convertToPresentation(Timestamp value, Class<? extends Date> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
		if (targetType != getPresentationType()) {
            throw new ConversionException("Converter only supports "
                    + getPresentationType().getName() + " (targetType was "
                    + targetType.getName() + ")");
        }

        if (value == null) {
            return null;
        }

        return new Date(value.getTime());
	}

	@Override
	public Class<Timestamp> getModelType() {
		return Timestamp.class;
	}

	@Override
	public Class<Date> getPresentationType() {
		return Date.class;
	}

}
