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
package org.eclipse.che.workspace.infrastructure.kubernetes.cache.tck;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.eclipse.che.commons.lang.NameGenerator.generate;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.eclipse.che.account.spi.AccountImpl;
import org.eclipse.che.api.core.model.workspace.WorkspaceStatus;
import org.eclipse.che.api.core.model.workspace.runtime.MachineStatus;
import org.eclipse.che.api.core.model.workspace.runtime.ServerStatus;
import org.eclipse.che.api.workspace.server.model.impl.ServerImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.workspace.infrastructure.kubernetes.model.KubernetesMachineImpl;
import org.eclipse.che.workspace.infrastructure.kubernetes.model.KubernetesRuntimeState;
import org.eclipse.che.workspace.infrastructure.kubernetes.model.KubernetesRuntimeState.RuntimeId;

/** @author Sergii Leshchenko */
public class TestObjects {

  public static AccountImpl createAccount() {
    return new AccountImpl(generate("id", 8), generate("name", 6), "any");
  }

  public static WorkspaceImpl createWorkspace() {
    return new WorkspaceImpl(
        generate("wsId", 8),
        createAccount(),
        new WorkspaceConfigImpl(
            generate("wsName", 8), "description", "defEnv", emptyList(), emptyList(), emptyMap()));
  }

  public static KubernetesRuntimeState createRuntimeState(WorkspaceImpl workspace) {
    return new KubernetesRuntimeState(
        new RuntimeId(workspace.getId(), "defEnv", workspace.getAccount().getId()),
        generate("namespace", 5),
        WorkspaceStatus.RUNNING);
  }

  public static KubernetesMachineImpl createMachine(
      String workspaceId,
      String machineName,
      MachineStatus status,
      Map<String, ServerImpl> servers) {
    return new KubernetesMachineImpl(
        workspaceId,
        machineName,
        generate("pod", 5),
        generate("container", 5),
        status,
        ImmutableMap.of("key1", "value1", generate("key", 2), generate("value", 2)),
        servers);
  }

  public static ServerImpl createServer(ServerStatus status) {
    return new ServerImpl(
        generate("http:://", 10),
        status,
        ImmutableMap.of(generate("key", 5), generate("value", 5)));
  }
}
