//    Task Executor is a simple script tasks executor.
//    Copyright (C) 2011 Adrián Romero Corchado.
//
//    This file is part of Task Executor
//
//    Task Executor is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Task Executor is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Task Executor. If not, see <http://www.gnu.org/licenses/>.

package com.adr.taskexecutor.engine;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author adrian
 */
public class LocaleFormats {

    private Locale locale;

    private boolean currencyafter;
    private int currencydecimals;
    private String currencysymbol;
    private int decimals;
    private String separator;
    private String grouping;
    private String percentagesymbol;

    private String datepattern;
    private String timepattern;
    private String datetimepattern;

    public LocaleFormats(Locale locale) {

        this.locale = locale;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
        SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat tf = (SimpleDateFormat)DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        SimpleDateFormat dtf = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);


        separator = new String(new char[]{symbols.getDecimalSeparator()});
        grouping = new String(new char[]{symbols.getGroupingSeparator()});
        decimals = nf.getMaximumFractionDigits();
        currencydecimals = cf.getCurrency().getDefaultFractionDigits();
        currencysymbol = cf.getCurrency().getSymbol();
        currencyafter = cf.format(0).endsWith(currencysymbol);
        percentagesymbol = new String(new char[]{symbols.getPercent()});
        datepattern = df.toPattern();
        timepattern = tf.toPattern();
        datetimepattern = dtf.toPattern();
    }
    
    public String getTrue() {
        return "true";
    }
    
    public String getFalse() {
        return "false";
    }

    public String getDatePattern() {
        return datepattern;
    }

    public String getTimePattern() {
        return timepattern;
    }

    public String getDateTimePattern() {
        return datetimepattern;
    }

    public String getPercentageSymbol() {
        return percentagesymbol;
    }
    
    public boolean getCurrencyAfter() {
        return currencyafter;
    }
    
    public String getCurrencySymbol() {
        return currencysymbol;
    }

    public int getCurrencyDecimals() {
        return currencydecimals;
    }

    public int getDecimals() {
        return decimals;
    }

    public String getSeparator() {
        return separator;
    }

    public String getGrouping() {
        return grouping;
    }

    public String formatDate(Date d, String pattern) {
        DateFormat dateformat = new SimpleDateFormat(pattern, locale);
        return dateformat.format(d);
    }

    public Date parseDate(String d, String pattern) throws ParseException {
        DateFormat dateformat = new SimpleDateFormat(pattern, locale);
        return dateformat.parse(d);
    }
}
