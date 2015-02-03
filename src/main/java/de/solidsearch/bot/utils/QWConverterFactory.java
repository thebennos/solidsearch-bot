package de.solidsearch.bot.utils;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class QWConverterFactory extends DefaultConverterFactory
{
	private static final long serialVersionUID = 8674102943204092571L;

	@Override
    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        // Handle String <-> Double
        if (presentationType == String.class && modelType == Integer.class) {
            return (Converter<PRESENTATION, MODEL>) new QWCleanNumberConverter();
        }
        // Let default factory handle the rest
        return super.findConverter(presentationType, modelType);
    }
}
