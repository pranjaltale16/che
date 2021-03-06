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
package org.eclipse.che.plugin.gdb.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.che.api.debugger.server.DebuggerFactory;
import org.eclipse.che.inject.DynaModule;

/** @author Anatoliy Bazko */
@DynaModule
public class GdbDebuggerModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), DebuggerFactory.class)
        .addBinding()
        .to(GdbDebuggerFactory.class);
  }
}
