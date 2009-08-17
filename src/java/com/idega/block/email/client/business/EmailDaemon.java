package com.idega.block.email.client.business;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.email.bean.FoundMessagesInfo;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.idegaweb.IWMainApplicationShutdownEvent;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.util.CoreConstants;
import com.idega.util.EventTimer;
import com.idega.util.StringUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.13 $
 * 
 * Last modified: $Date: 2009/01/28 12:19:01 $ by $Author: juozas $
 */

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class EmailDaemon implements ApplicationContextAware, ApplicationListener, ActionListener {

	private static final Logger LOGGER = Logger.getLogger(EmailDaemon.class.getName());
	public static final String THREAD_NAME = "email_daemon";
	
	private EventTimer emailTimer;
	private final ReentrantLock lock = new ReentrantLock();

	@Autowired
	private EmailSubjectPatternFinder emailFinder;
	private ApplicationContext ctx;

	public static final String PROP_MAIL_HOST = "mail_host";
	private static final String PROP_SYSTEM_PROTOCOL = "mail_protocol";
	private static final String PROP_SYSTEM_PASSWORD = "mail_password";

	public void start() {

		try {
			long defaultCheckInterval = EventTimer.THREAD_SLEEP_5_MINUTES;
			String checkIntervalStr = IWMainApplication.getDefaultIWMainApplication().getSettings()
				.getProperty("email_daemon_check_interval", String.valueOf(defaultCheckInterval));

			long checkInterval;

			if (CoreConstants.EMPTY.equals(checkIntervalStr))
				checkInterval = defaultCheckInterval;
			else
				checkInterval = new Long(checkIntervalStr);

			emailTimer = new EventTimer(checkInterval, THREAD_NAME);
			emailTimer.addActionListener(this);
			emailTimer.start(checkInterval);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while starting up email daemon", e);
		}
	}

	public void actionPerformed(ActionEvent event) {
		try {

			if (event.getActionCommand().equalsIgnoreCase(THREAD_NAME)) {

				if (!lock.isLocked()) {
//					locking for long running checks in the inbox (lots of messages). skipping processing, if it's already under processing (locked)
					lock.lock();

					try {
						IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
						String host = settings.getProperty(PROP_MAIL_HOST, CoreConstants.EMPTY);
						String accountName = settings.getProperty(CoreConstants.PROP_SYSTEM_ACCOUNT, CoreConstants.EMPTY);
						String protocol = settings.getProperty(PROP_SYSTEM_PROTOCOL, CoreConstants.EMPTY);
						String password = settings.getProperty(PROP_SYSTEM_PASSWORD, CoreConstants.EMPTY);

						if (StringUtil.isEmpty(host)) {
							return;
						}
						if (StringUtil.isEmpty(accountName) || StringUtil.isEmpty(protocol) || StringUtil.isEmpty(password)) {
							LOGGER.log(Level.WARNING, "Mail properties are empty");
							return;
						}

						EmailSubjectPatternFinder emailFinder = getEmailFinder();
						EmailParams params = emailFinder.login(host, accountName, password, protocol);
						// Getting message map
						Map<String, FoundMessagesInfo> messages = emailFinder.getMessageMap(params);
						if ((messages != null) && (!messages.isEmpty())) {
							ApplicationEmailEvent eventEmail = new ApplicationEmailEvent(this);
							eventEmail.setMessages(messages);
							eventEmail.setEmailParams(params);
							ctx.publishEvent(eventEmail);
						} else {
							emailFinder.logout(params);
						}
					} finally {
						lock.unlock();
					}
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while processing emails found in the inbox", e);
		}

	}

	public void stop() {

		if (this.emailTimer != null) {
			this.emailTimer.stop();
			this.emailTimer = null;
		}

	}

	public void onApplicationEvent(ApplicationEvent applicationevent) {

		if (applicationevent instanceof IWMainApplicationStartedEvent) {
			start();

		} else if (applicationevent instanceof IWMainApplicationShutdownEvent) {
			stop();
		}
	}

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		ctx = applicationcontext;
	}

	public EmailSubjectPatternFinder getEmailFinder() {
		return emailFinder;
	}
}