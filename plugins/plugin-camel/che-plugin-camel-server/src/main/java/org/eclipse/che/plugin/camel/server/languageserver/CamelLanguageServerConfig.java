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
package org.eclipse.che.plugin.camel.server.languageserver;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.che.api.languageserver.DefaultInstanceProvider;
import org.eclipse.che.api.languageserver.LanguageServerConfig;
import org.eclipse.che.api.languageserver.ProcessCommunicationProvider;
import org.eclipse.che.plugin.camel.server.inject.CamelModule;

/** Launcher for Apache Camel Language Server */
@Singleton
public class CamelLanguageServerConfig implements LanguageServerConfig {
  private static final String REGEX = ".*\\.(xml)";

  private final Path launchScript;

  @Inject
  public CamelLanguageServerConfig() {

    this.launchScript = Paths.get(System.getenv("HOME"), "che/ls-camel/launch.sh");
  }

  @Override
  public RegexProvider getRegexpProvider() {
    return new RegexProvider() {
      @Override
      public Map<String, String> getLanguageRegexes() {
        return ImmutableMap.of(CamelModule.LANGUAGE_ID, REGEX);
      }

      @Override
      public Set<String> getFileWatchPatterns() {
        return ImmutableSet.of();
      }
    };
  }

  @Override
  public CommunicationProvider getCommunicationProvider() {
    ProcessBuilder processBuilder = new ProcessBuilder(launchScript.toString());
    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

    return new ProcessCommunicationProvider(processBuilder, CamelModule.LANGUAGE_ID);
  }

  @Override
  public InstanceProvider getInstanceProvider() {
    return DefaultInstanceProvider.getInstance();
  }

  @Override
  public InstallerStatusProvider getInstallerStatusProvider() {
    return new InstallerStatusProvider() {
      @Override
      public boolean isSuccessfullyInstalled() {
        return launchScript.toFile().exists();
      }

      @Override
      public String getCause() {
        return isSuccessfullyInstalled() ? null : "Launch script file does not exist";
      }
    };
  }
}
