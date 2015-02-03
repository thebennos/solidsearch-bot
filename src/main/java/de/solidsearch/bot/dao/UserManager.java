package de.solidsearch.bot.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.data.User;
import de.solidsearch.bot.data.UserGroup;

@Component("UserManager")
@Scope(value = "prototype")
public class UserManager implements Serializable
{
	private static final long serialVersionUID = 4141602333890891853L;

	@Autowired
	SessionFactory sessionFactory;

	public User getUserByEmail(String email)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(User.class).add(Restrictions.eq("email", email));
			@SuppressWarnings("unchecked")
			List<User> uL = (List<User>) crit.list();
			tx.commit();
			if (uL.isEmpty())
				return null;
			return uL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public User getUserByID(long id)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(User.class).add(Restrictions.eq("id", id));
			@SuppressWarnings("unchecked")
			List<User> uL = (List<User>) crit.list();
			tx.commit();
			if (uL.isEmpty())
				return null;
			return uL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public UserGroup getUserGroupByID(long id)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(UserGroup.class).add(Restrictions.eq("id", id));
			@SuppressWarnings("unchecked")
			List<UserGroup> uL = (List<UserGroup>) crit.list();
			tx.commit();
			if (uL.isEmpty())
				return null;
			return uL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public List<User> getAllUser()
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(User.class);
			@SuppressWarnings("unchecked")
			List<User> uL = (List<User>) crit.list();
			tx.commit();
			return uL;
		} finally
		{
			if (session != null)
				session.close();
		}
	}
	
	public void deleteUser(User user)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.delete(user);
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public void saveOrUpdateUser(User user)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(user);
			tx.commit();
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public User getUserByActivationToken(String activationToken)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(User.class).add(Restrictions.eq("activationToken", activationToken));
			@SuppressWarnings("unchecked")
			List<User> uL = (List<User>) crit.list();
			tx.commit();
			if (uL.isEmpty())
				return null;
			return uL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}

	public User getUserByPasswordResetToken(String passwordResetToken)
	{
		Session session = null;
		try
		{
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			Criteria crit = session.createCriteria(User.class).add(Restrictions.eq("passwordResetToken", passwordResetToken));
			@SuppressWarnings("unchecked")
			List<User> uL = (List<User>) crit.list();
			tx.commit();
			if (uL.isEmpty())
				return null;
			return uL.get(0);
		} finally
		{
			if (session != null)
				session.close();
		}
	}	
	
}
