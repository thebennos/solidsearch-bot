package de.solidsearch.bot.data;

public class Image
{

	private String URLName = "";

	private String foundAtURL = "";
	
	private String title = "";
	
	private String alt = "";
	
	private int width = 0;
	
	private int height = 0;
	
	private boolean relevant = false;

	private boolean aboveTheFold = false;
	
	public String getURLName()
	{
		return URLName;
	}

	public void setURLName(String uRLName)
	{
		URLName = uRLName;
	}

	public String getFoundAtURL()
	{
		return foundAtURL;
	}

	public void setFoundAtURL(String foundAtURL)
	{
		this.foundAtURL = foundAtURL;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getAlt()
	{
		return alt;
	}

	public void setAlt(String alt)
	{
		this.alt = alt;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public boolean isRelevant()
	{
		return relevant;
	}

	public void setRelevant(boolean relevant)
	{
		this.relevant = relevant;
	}

	public boolean isAboveTheFold()
	{
		return aboveTheFold;
	}

	public void setAboveTheFold(boolean aboveTheFold)
	{
		this.aboveTheFold = aboveTheFold;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Image)
		{
			if (((Image) obj).getURLName().equalsIgnoreCase(this.URLName))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public int hashCode()
	{
		return (URLName.hashCode()) * 17;
	}

}
