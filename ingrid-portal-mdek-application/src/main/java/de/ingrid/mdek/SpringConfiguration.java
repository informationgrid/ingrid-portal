/*
 * **************************************************-
 * Ingrid Management iPlug
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.tngtech.configbuilder.ConfigBuilder;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.comm.ICodeListCommunication;
import de.ingrid.codelists.comm.IngridCLCommunication;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.mdek.persistence.db.DaoFactory;
import de.ingrid.mdek.security.AuthenticationProvider;
import de.ingrid.mdek.security.PortalAuthenticationProvider;
import de.ingrid.mdek.security.TomcatAuthenticationProvider;
import de.ingrid.mdek.upload.auth.AuthService;
import de.ingrid.mdek.upload.auth.PortalAuthService;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.impl.FileSystemStorage;
import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.userrepo.DbUserRepoManager;
import de.ingrid.mdek.userrepo.UserRepoManager;

@Configuration
public class SpringConfiguration {

	@Profile("http")
	public static class HttpCommunicationProfile {

		@Bean
		public ICodeListCommunication httpCommunication(Config config) {
			HttpCLCommunication communication = new HttpCLCommunication();
			communication.setRequestUrl(config.codelistRequestUrl);
			communication.setUsername(config.codelistUsername);
			communication.setPassword(config.codelistPassword);
			return communication;
		}
	}

	@Profile("ingrid")
	public static class IngridCommunicationProfile {

		@Bean
		public ICodeListCommunication httpCommunication() {
		    return new IngridCLCommunication();
		}
	}

	@Bean
	public Config globalConfig() {
		Config config = new ConfigBuilder<Config>(Config.class).build();
		config.initialize();
		return config;
	}

	@Bean
	public CodeListService codeListService(ICodeListCommunication communication, Config config,
			List<ICodeListPersistency> persistencies) {
		CodeListService service = new CodeListService();

		service.setPersistencies(persistencies);
		service.setComm(communication);
		service.setDefaultPersistency(config.codelistDefaultPersistency);
		return service;
	}

	@Bean
	public AuthenticationProvider authenticationProvider(Config config, UserRepoManager manager) {
		if (config.noPortal) {
			TomcatAuthenticationProvider provider = new TomcatAuthenticationProvider();
			provider.setRepoManager(manager);
			return provider;
		} else {
			return new PortalAuthenticationProvider();
		}
	}

	@Bean
	public UserRepoManager userRepoManager(Config config, @Value("#{daoFactory}") DaoFactory dao) {
		// always create manager to prevent NullPointerException
		DbUserRepoManager manager = new DbUserRepoManager();
		manager.setDaoFactory(dao);
		return manager;
	}

	/**
	 * Upload service
	 */

	@Bean
	public AuthService authService(Config config) {
	    return new PortalAuthService();
	}

	@Bean
	public Storage storage(Config config) {
		switch (config.uploadImpl) {
		case "de.ingrid.mdek.upload.storage.impl.FileSystemStorage":
		default:
			final FileSystemStorage instance = new FileSystemStorage();
			instance.setDocsDir(config.uploadDocsDir);
			instance.setPartsDir(config.uploadPartsDir);

			// validators
			final List<Validator> validators = new ArrayList<Validator>();
			final Map<String, Validator> uploadValidatorMap = config.uploadValidatorMap;
			for (final String validatorName : config.uploadValidators) {
				if (uploadValidatorMap.containsKey(validatorName)) {
	 				validators.add(uploadValidatorMap.get(validatorName));
				}
				else {
					throw new BeanCreationException("Error creating upload instance: A validator with name '"+validatorName+"' is not defined in 'upload.validators.config'.");
				}
			}
			instance.setValidators(validators);

			return instance;
		}
	}
}
