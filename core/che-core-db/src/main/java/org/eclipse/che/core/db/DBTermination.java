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
package org.eclipse.che.core.db;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import java.lang.reflect.Field;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.coordination.jgroups.JGroupsRemoteConnection;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stops {@link PersistService} when a system is ready to shutdown.
 *
 * @author Anton Korneta
 */
@Singleton
public class DBTermination {

  private static final Logger LOG = LoggerFactory.getLogger(DBTermination.class);

  private final PersistService persistService;
  private final EntityManagerFactory emFactory;

  @Inject
  public DBTermination(PersistService persistService, EntityManagerFactory emFactory) {
    this.persistService = persistService;
    this.emFactory = emFactory;
  }

  /** Stops {@link PersistService}. Any DB operations are impossible after that. */
  public void terminate() {
    try {
      LOG.info("Stopping persistence service.");
      fixJChannelClosing(emFactory);
      persistService.stop();
    } catch (RuntimeException ex) {
      LOG.error("Failed to stop persistent service. Cause: " + ex.getMessage());
    }
  }

  /**
   * This method is hack that changes value of {@link JGroupsRemoteConnection#isLocal} to false.
   * This is needed to close the JGroups EclipseLinkCommandChannel and as result gracefully stop of
   * the system.<br>
   * For more details see {@link JGroupsRemoteConnection#closeInternal()}
   *
   * <p>The corresponding eclipse-link extension issue
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=534148
   */
  private void fixJChannelClosing(EntityManagerFactory emFactory) {
    try {
      final AbstractSession session = emFactory.unwrap(AbstractSession.class);
      CommandManager commandManager = session.getCommandManager();
      if (commandManager == null) {
        // not cluster mode
        return;
      }
      final JGroupsRemoteConnection conn =
          (JGroupsRemoteConnection) commandManager.getTransportManager().getConnectionToLocalHost();
      final Field isLocal = conn.getClass().getDeclaredField("isLocal");
      isLocal.setAccessible(true);
      isLocal.set(conn, false);
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      LOG.error(
          "Failed to change JGroupsRemoteConnection#isLocal this may prevent the graceful stop of "
              + "the system because EclipseLinkCommandChannel won't be closed. Cause: "
              + ex.getMessage());
    }
  }
}
