package de.solidsearch.bot.data.enumerations;


public enum URLField
{
	TITLE,METADESCRIPTION,ONPAGETEXT,INDEXNOINDEX, PAGESIZE, MAINKEYWORD, HTTPSTATUSCODE,CANONICALTAG, BLOCKEDBYROBOTSTXT, REDIRECTEDTO;
	
	public int getVector()
	{
		int vector = -1;
		switch (this)
		{
		case TITLE:
			vector = 0x01; // 0001
			break;
		case METADESCRIPTION:
			vector = 0x02; // 0010
			break;
		case ONPAGETEXT:
			vector = 0x04; // 0100
			break;
		case HTTPSTATUSCODE:
			vector = 0x08;  // 1000
			break;
		case INDEXNOINDEX:
			vector = 0x10; // 0001 0000
			break;
		case PAGESIZE:
			vector = 0x20; // 0010 0000
			break;
		case MAINKEYWORD:
			vector = 0x40; // 0100 0000
			break;
		case CANONICALTAG:
			vector = 0x80; // 1000 0000
			break;
		case BLOCKEDBYROBOTSTXT:
			vector = 0x100; // 0001 0000 0000
			break;
		case REDIRECTEDTO:
			vector = 0x200; // 0010 0000 0000
			break;
		default:
			break;
		}
		return vector;
	}
	
	public boolean isEnabledInVector(int vector)
	{
		if ((vector & this.getVector()) == this.getVector())
			return true;
		else 
			return false;
	}
}
