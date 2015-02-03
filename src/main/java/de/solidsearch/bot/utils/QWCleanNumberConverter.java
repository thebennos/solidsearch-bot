package de.solidsearch.bot.utils;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.util.converter.StringToIntegerConverter;

public class QWCleanNumberConverter extends StringToIntegerConverter
{
	private static final long serialVersionUID = 6691100239124636393L;

	@Override
    protected NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        return format;
    }
}
