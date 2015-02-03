package de.solidsearch.bot.data;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Alarm implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private static final long serialVersionUID = -3538625863840760363L;

	private String alarmKey = null;
	
	private ArrayList<String> alarmValues = null;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "alarmListId", nullable = false)
	private AlarmList alarmList;

	public Alarm()
	{

	}

	public Alarm(String alarmKey, ArrayList<String> alarmValues, AlarmList alarmList)
	{
		this.alarmKey = alarmKey;
		this.alarmValues = alarmValues;
		this.alarmList = alarmList;
	}

	public String getAlarmKey()
	{
		return alarmKey;
	}

	public void setAlarmKey(String alarmKey)
	{
		this.alarmKey = alarmKey;
	}

	public ArrayList<String> getAlarmValues()
	{
		return alarmValues;
	}

	public void setAlarmValues(ArrayList<String> alarmValues)
	{
		this.alarmValues = alarmValues;
	}

	public AlarmList getAlarmList()
	{
		return alarmList;
	}

	public void setAlarmList(AlarmList alarmList)
	{
		this.alarmList = alarmList;
	}

}
