package de.solidsearch.bot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import de.solidsearch.bot.dao.ProjectManager;
import de.solidsearch.bot.dao.UserManager;
import de.solidsearch.bot.data.User;
import de.solidsearch.bot.data.UserGroup;

@Component
public class BotInitializer implements ApplicationListener<ContextRefreshedEvent>
{
	@Autowired
	BotConfig config;
	
	@Autowired
	UserManager userManager;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0)
	{
		initAdminUserIfNotExists();		
	}
	
	private void initAdminUserIfNotExists()
	{
		User myuser = userManager.getUserByEmail(config.ADMIN_USER);

		if (myuser == null)
		{
			// first user
			myuser = new User();
			UserGroup myGroup = new UserGroup();
			myuser.setAdmin(true);
			myuser.setFirstname("Admin");
			myuser.setLastname("User");
			myuser.setEmail(config.ADMIN_USER);
			StandardPasswordEncoder sp = new StandardPasswordEncoder();
			myuser.setPassword(sp.encode(config.ADMIN_PASSWORD));
			myuser.setDisabled(false);
			myuser.setUserGroup(myGroup);
			userManager.saveOrUpdateUser(myuser);

			// generate empty project
			ProjectManager projectManager = (ProjectManager) AppContext.getApplicationContext().getBean("ProjectManager", ProjectManager.class);
			projectManager.createProject(myuser, false);

			myGroup.setMaxProjects(50);
			userManager.saveOrUpdateUser(myuser);
		}
	}	
}
