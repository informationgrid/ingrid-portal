/*
 * **************************************************-
 * Ingrid Management iPlug
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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

import java.io.IOException;
import java.util.List;

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
import de.ingrid.mdek.upload.storage.FileSystemStorage;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.userrepo.DbUserRepoManager;

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
			IngridCLCommunication communication = new IngridCLCommunication();
			return communication;
		}
	}

	@Bean
	public Config globalConfig() {
		Config config = new ConfigBuilder<Config>(Config.class).build();
		try {
			config.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public AuthenticationProvider authenticationProvider(Config config, @Value("#{daoFactory}") DaoFactory dao) {
		if (config.noPortal) {
			TomcatAuthenticationProvider provider = new TomcatAuthenticationProvider();
			DbUserRepoManager manager = new DbUserRepoManager();
			manager.setDaoFactory(dao);
			provider.setRepoManager(manager);
			return provider;
		} else {
			return new PortalAuthenticationProvider();
		}
	}

	@Bean
	public Storage storage(Config config) {
		Storage instance = null;
		switch (config.uploadImpl) {
		case "FileSystemStorage":
		default:
			instance = new FileSystemStorage(config.uploadBaseDir);

		}
		return instance;
	}
}
