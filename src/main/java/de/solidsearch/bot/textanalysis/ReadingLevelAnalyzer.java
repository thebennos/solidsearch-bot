package de.solidsearch.bot.textanalysis;


public class ReadingLevelAnalyzer
{

	
	public short getReadingLevel(String text)
	{
		int wordLenth = 0;
		int wordsOfSentence = 1;
		
		int wordLengthSum = 0;
		int wordCount = 1;
		int sentenceLengthSum = 0;
		int sentenceCount = 1;
		int commaCount = 0;
				
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			
			if (c == ' ')
			{
				// end of text
				if (i+1 >= text.length())
				{
					sentenceLengthSum += wordsOfSentence;
					continue;
				}
				else if (i+2 < text.length())
				{
					char c2 = text.charAt(i+1);
					if (c2 == '.' || c2 == '!' || c2 == '?')
					{
						continue;
					}
				}
				
				// new word
				wordsOfSentence++;
				wordLengthSum += wordLenth;
				wordCount++;
				wordLenth = 0;
			}
			else if (c == '.' || c == '!' || c == '?')
			{
				// end of text
				if (i+1 >= text.length())
				{
					sentenceLengthSum += wordsOfSentence;
					continue;
				}
				
				// sentence-end sign
				if (sentenceEndCheck(c,text,i))
				{
					sentenceLengthSum += wordsOfSentence;
					sentenceCount++;
					wordsOfSentence = 1;

					if (text.charAt(i+1) != ' ')
					{
						wordLengthSum += wordLenth;
						wordCount++;
						wordLenth = 0;
					}
				}
			}
			else if (c == ',')
			{
				commaCount++;
			}
			else
			{
				wordLenth++;
			}
			
		}
		
		float readingLevel = 0.25f;
		if (wordCount > 1000)
			readingLevel += 0.25;
		if (wordCount > 700)
			readingLevel += 0.25;
		if (wordCount > 250)
			readingLevel += 0.25;
		
		float avgWordLength = (float)wordLengthSum / (float)wordCount;
		
		if (avgWordLength > 4.5)
			readingLevel += 0.25;
		if (avgWordLength > 5)
			readingLevel += 0.25;
		if (avgWordLength > 5.5)
			readingLevel += 0.25;
		
		float avgSentenceLength = (float)sentenceLengthSum / (float)sentenceCount;
		
		if (avgSentenceLength > 12)
			readingLevel += 0.25;
		if (avgSentenceLength > 14)
			readingLevel += 0.25;
		if (avgSentenceLength > 18)
			readingLevel += 0.5;
		if (avgSentenceLength > 22)
			readingLevel += 0.5;
		if (avgSentenceLength > 25)
			readingLevel += 0.5;
		
		float sentenceCommaQuotient = (float)sentenceCount / (float)commaCount;
		
		if (sentenceCommaQuotient < 1.7)
			readingLevel += 0.25;
		if (sentenceCommaQuotient < 1.4)
			readingLevel += 0.25;
		if (sentenceCommaQuotient < 1.2)
			readingLevel += 0.25;
		if (sentenceCommaQuotient < 1.0)
			readingLevel += 0.5;
		
//		System.out.println("Text:\n " + text + "\n");
//		System.out.println("Words:" + wordCount);
//		System.out.println("Avg. Word length: " + wordLengthSum / wordCount);
//		System.out.println("Sentences: " + sentenceCount);
//		System.out.println("Avg. sentence length: " + sentenceLengthSum / sentenceCount + " avgSentenceLength: " + avgSentenceLength);
//		System.out.println("Commas: " + commaCount + " sentenceCommaQuotient: " + sentenceCommaQuotient);
//		System.out.println("ReadingLevel: " + Math.round(readingLevel) + " / " + readingLevel);
//		System.out.println("--------------");

		return (short) Math.round(readingLevel);
	}
	
	private boolean sentenceEndCheck(char currentChar,String input, int currentPosition)
	{
		boolean isEndOf = false;
		
		if (currentPosition < 1)
		{
			return isEndOf;
		}
		else if (currentPosition -1 < input.length() && currentPosition + 2 < input.length())
		{
			String before = Character.toString(input.charAt(currentPosition-1));
						
			if (currentChar == '.')
			{
				try 
				{
					// we have this (1.) case : Im 1. Lebensjahr 
					Integer.parseInt(before);
					return false;
				} 
				catch (NumberFormatException e) {}
			}		

			if (Character.isUpperCase(input.charAt(currentPosition+1)))
			{
				// new sentence with this pattern: (.A)
				isEndOf = true;
			}
			else if (Character.isUpperCase(input.charAt(currentPosition+2)) && input.charAt(currentPosition+1) == ' ')
			{
				// new sentence with this pattern: (. A)
				isEndOf = true;
			}
		} 
		else
		{
			// end of file
			isEndOf = true;
		}
		
		return isEndOf;
	}
}
