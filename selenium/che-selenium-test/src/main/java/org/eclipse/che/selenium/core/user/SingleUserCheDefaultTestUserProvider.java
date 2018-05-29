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
import java.io.IOException;
import javax.inject.Singleton;
import org.eclipse.che.selenium.core.provider.DefaultTestUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides {@link DefaultTestUser} for the Single User Eclipse Che which ought to be existed at the
 * start of test execution. All tests share the same default user.
 *
 * @author Dmytro Nochevnov
 */
@Singleton
public class SingleUserCheDefaultTestUserProvider implements DefaultTestUserProvider {
  private static final Logger LOG =
      LoggerFactory.getLogger(SingleUserCheDefaultTestUserProvider.class);
  private final DefaultTestUser defaultTestUser;

  @Inject
  public SingleUserCheDefaultTestUserProvider(
      TestUserFactory<DefaultTestUser> defaultTestUserFactory) {
    String name = "che";
    String email = "che@eclipse.org";
    String password = "secret";
    String offlineToken = "";
    this.defaultTestUser = defaultTestUserFactory.create(name, email, password, offlineToken, this);

    LOG.info("User name='{}', id='{}' is being used by default", name, defaultTestUser.getId());
  }

  @Override
  public DefaultTestUser get() {
    return defaultTestUser;
  }

  @Override
  public void delete() throws IOException {
    // we don't need to remove test user of Single User Eclipse Che
  }
}
