package de.solidsearch.bot.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class AlarmList implements Serializable
{
	private static final long serialVersionUID = -1426363927332002744L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long alarmListId;
	
    @OneToMany(cascade=CascadeType.ALL, mappedBy="alarmList")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OnDelete(action=OnDeleteAction.CASCADE)
	private List<Alarm> alarms = new ArrayList<Alarm>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "projectSummaryId", nullable = false)
	private ProjectSummary projectSummary;
	
	private Integer projectSummaryInfoCode;
	
	public AlarmList()
	{
		
	}
	
	public List<Alarm> getAlarms()
	{
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms)
	{
		this.alarms = alarms;
	}
	
	public Alarm get(int i)
	{
		return alarms.get(i);
	}
	
	public void add(Alarm alarm)
	{
		alarms.add(alarm);
	}
	
	public boolean isEmpty()
	{
		return alarms.isEmpty();
	}
	
	public int size()
	{
		return alarms.size();
	}

	public ProjectSummary getProjectSummary()
	{
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary)
	{
		this.projectSummary = projectSummary;
	}

	public Integer getProjectSummaryInfoCode()
	{
		return projectSummaryInfoCode;
	}

	public void setProjectSummaryInfoCode(Integer projectSummaryInfoCode)
	{
		this.projectSummaryInfoCode = projectSummaryInfoCode;
	}

	public long getAlarmListId()
	{
		return alarmListId;
	}
	
}
