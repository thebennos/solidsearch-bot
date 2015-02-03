package de.solidsearch.bot.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Index;


@Entity
@Table(name="appuser")
public class User implements Serializable
{
	private static final long serialVersionUID = 1591378873577531641L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String firstname;
	String lastname;
	String password;
	@Index(name="email_idx")
	String email;
	Date expires;
	boolean admin;
	boolean readOnly;
	boolean primaryUser;
	boolean trialUser;
	boolean disabled;
	long selectedProjectId;
	Calendar selectedProjectDate;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	UserGroup userGroup;
	String activationToken;
	String passwordResetToken;
	Calendar activationRequestTime;
	Calendar passwordResetRequestTime;
	String uiDatabaseId = "";

	public User()
	{
		this.firstname = "";
		this.lastname = "";
		this.password = "";
		this.email = "";
		this.admin = false;
		this.trialUser = false;
		this.expires = null;
		this.disabled = true;
		this.password = "";	
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAdmin(boolean admin)
	{
		this.admin = admin;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}

	public Date getExpires()
	{
		return expires;
	}

	public void setExpires(Date expires)
	{
		this.expires = expires;
	}

	public boolean isTrialUser()
	{
		return trialUser;
	}

	public void setTrialUser(boolean trialUser)
	{
		this.trialUser = trialUser;
	}

	public boolean isDisabled()
	{
		return disabled;
	}

	public void setDisabled(boolean disabled)
	{
		this.disabled = disabled;
	}

	public UserGroup getUserGroup()
	{
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup)
	{
		this.userGroup = userGroup;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	public boolean isPrimaryUser()
	{
		return primaryUser;
	}

	public void setPrimaryUser(boolean primaryUser)
	{
		this.primaryUser = primaryUser;
	}

	public long getSelectedProjectId()
	{
		return selectedProjectId;
	}

	public void setSelectedProjectId(long selectedProjectId)
	{
		this.selectedProjectId = selectedProjectId;
	}
	
	public Calendar getSelectedProjectDate()
	{
		return selectedProjectDate;
	}

	public void setSelectedProjectDate(Calendar selectedProjectDate)
	{
		this.selectedProjectDate = selectedProjectDate;
	}

	public String getActivationToken()
	{
		return activationToken;
	}

	public void setActivationToken(String activationToken)
	{
		this.activationToken = activationToken;
	}

	public Calendar getActivationRequestTime()
	{
		return activationRequestTime;
	}

	public void setActivationRequestTime(Calendar activationRequestTime)
	{
		this.activationRequestTime = activationRequestTime;
	}

	public String getPasswordResetToken()
	{
		return passwordResetToken;
	}

	public void setPasswordChangeToken(String passwordResetToken)
	{
		this.passwordResetToken = passwordResetToken;
	}

	public Calendar getPasswordResetRequestTime()
	{
		return passwordResetRequestTime;
	}

	public void setPasswordResetRequestTime(Calendar passwordResetRequestTime)
	{
		this.passwordResetRequestTime = passwordResetRequestTime;
	}

	/**
	 * Table name for detailed URL data. 
	 * @return url table suffix or null if no data available
	 */
	public String getUiDatabaseId()
	{
		return uiDatabaseId;
	}

	public void setUiDatabaseId(String uiDatabaseId)
	{
		this.uiDatabaseId = uiDatabaseId;
	}
}