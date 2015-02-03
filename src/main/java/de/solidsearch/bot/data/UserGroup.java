package de.solidsearch.bot.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserGroup implements Serializable
{
	private static final long serialVersionUID = 1591378873577531641L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	private int maxProjects = 50;
	private int currentProjects = 0;

	public UserGroup()
	{
		
	}
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public int getMaxProjects()
	{
		return maxProjects;
	}

	public void setMaxProjects(int maxProjects)
	{
		this.maxProjects = maxProjects;
	}

	public int getCurrentProjects()
	{
		return currentProjects;
	}

	public void setCurrentProjects(int currentProjects)
	{
		this.currentProjects = currentProjects;
	}	
}