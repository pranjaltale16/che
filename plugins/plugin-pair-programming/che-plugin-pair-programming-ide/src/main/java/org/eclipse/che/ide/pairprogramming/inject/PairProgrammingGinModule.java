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
package org.eclipse.che.ide.pairprogramming.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.pairprogramming.view.PairProgrammingView;
import org.eclipse.che.ide.pairprogramming.view.PairProgrammingViewImpl;

@ExtensionGinModule
public class PairProgrammingGinModule extends AbstractGinModule {

  @Override
  protected void configure() {
    bind(PairProgrammingView.class).to(PairProgrammingViewImpl.class);
  }
}
