package de.solidsearch.bot.data.enumerations;

public enum ReadingLevel
{
	PRIMARYSCHOOL,YELLOWPRESS,NEWSPAPER,MAGAZIN,SCIENCE;

	public short getReaderLevelScore()
	{
		short level = -1;
		switch (this)
		{
		case PRIMARYSCHOOL:
			level = 0;
			break;
		case YELLOWPRESS:
			level = 1;
			break;
		case NEWSPAPER:
			level = 2;
			break;
		case MAGAZIN:
			level = 3;
			break;
		case SCIENCE:
			level = 4;
			break;
			
		default:
			level = -1;
			break;
		}
		return level;
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
