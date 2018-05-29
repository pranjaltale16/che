/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.core.user;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.IOException;
import javax.inject.Singleton;
import org.eclipse.che.selenium.core.client.KeycloakAdminConsoleClient;
import org.eclipse.che.selenium.core.provider.DefaultTestUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link DefaultTestUser} for the Multi User Eclipse Che which ought to be existed at the
 * start of test execution. All tests share the same default user.
 *
 * @author Dmytro Nochevnov
 */
@Singleton
public class MultiUserCheDefaultTestUserProvider implements DefaultTestUserProvider {

  private static final Logger LOG =
      LoggerFactory.getLogger(MultiUserCheDefaultTestUserProvider.class);

  private final DefaultTestUser defaultTestUser;
  private final boolean isNewUser;
  private final KeycloakAdminConsoleClient keycloakAdminConsoleClient;

  @Inject
  public MultiUserCheDefaultTestUserProvider(
      TestUserFactory<DefaultTestUser> defaultTestUserFactory,
      KeycloakAdminConsoleClient keycloakAdminConsoleClient,
      MultiUserCheAdminTestUserProvider adminTestUserProvider,
      @Named("che.testuser.name") String name,
      @Named("che.testuser.email") String email,
      @Named("che.testuser.password") String password,
      @Named("che.testuser.offline_token") String offlineToken) {
    this.keycloakAdminConsoleClient = keycloakAdminConsoleClient;
    if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
      DefaultTestUser testUser;
      Boolean isNewUser;
      try {
        testUser = keycloakAdminConsoleClient.createDefaultUser(this);
        isNewUser = true;
      } catch (IOException e) {
        LOG.warn(
            "Default test user credentials isn't set and it's impossible to create it from tests because of error. "
                + "Is going to use admin test user as a default test user.",
            e);

        isNewUser = false;

        AdminTestUser adminTestUser = adminTestUserProvider.get();
        testUser =
            defaultTestUserFactory.create(
                adminTestUser.getName(),
                adminTestUser.getEmail(),
                adminTestUser.getPassword(),
                adminTestUser.getOfflineToken(),
                adminTestUserProvider);
      }

      this.defaultTestUser = testUser;
      this.isNewUser = isNewUser;
    } else {
      this.defaultTestUser =
          defaultTestUserFactory.create(name, email, password, offlineToken, this);
      this.isNewUser = false;

      LOG.info(
          "User name='{}', id='{}' is being used as default user",
          defaultTestUser.getName(),
          defaultTestUser.getId());
    }
  }

  @Override
  public DefaultTestUser get() {
    return defaultTestUser;
  }

  @Override
  public void delete() throws IOException {
    if (isNewUser) {
      keycloakAdminConsoleClient.delete(defaultTestUser);
    }
  }
}
