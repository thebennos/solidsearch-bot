package de.solidsearch.bot.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import de.solidsearch.bot.data.enumerations.URLField;
import de.solidsearch.shared.data.KeywordStem;
import de.solidsearch.shared.textanalysis.Stemmer;
import de.solidsearch.shared.textanalysis.TextNormalizer;
//
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter;
//import org.apache.lucene.analysis.core.LowerCaseFilter;
//import org.apache.lucene.analysis.de.GermanMinimalStemFilter;
//import org.apache.lucene.analysis.de.GermanNormalizationFilter;
//import org.apache.lucene.analysis.standard.ClassicTokenizer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.util.CharArraySet;
//import org.apache.lucene.util.Version;
import de.solidsearch.shared.utils.QWLocale;

public class Tester
{

	public static void main(String[] args) throws IOException
	{
		System.out.println("http://www.hallo-eltern.de/m_baby/wachstumsschub1.htm".replaceAll("[^\\p{L}]+", " "));
		System.out.println("http://www.hallo-eltern.de/m_baby/wachstumsschub1.Html".replaceAll("(?i).html|(?i).htm", " "));
	}


}
