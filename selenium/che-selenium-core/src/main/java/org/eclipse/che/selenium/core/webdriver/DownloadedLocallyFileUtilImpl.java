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
package org.eclipse.che.selenium.core.webdriver;

import static com.google.common.io.Files.createTempDir;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Paths.*;
import static java.util.Arrays.stream;
import static org.eclipse.che.commons.lang.ZipUtils.unzip;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * This is set of methods which operate with files which are downloaded by WebDriver which is
 * running locally.
 *
 * @author Dmytro Nochevnov
 */
@Singleton
public class DownloadedLocallyFileUtilImpl implements DownloadedFileUtil {
  private final String downloadDir;

  @Inject
  public DownloadedLocallyFileUtilImpl(@Named("tests.download_dir") String downloadDir) {
    this.downloadDir = downloadDir;
  }

  @Override
  public List<String> getPackageFileList(String webDriverSessionId, String downloadedPackageName)
      throws IOException {
    Path tempDirectory = Paths.get(createTempDir().toString());
    File packageFile = Paths.get(downloadDir).resolve(downloadedPackageName).toFile();
    unzip(packageFile, tempDirectory.toFile());

    List<String> packageFileList =
        FileUtils.listFiles(tempDirectory.toFile(), null, true)
            .stream()
            .map(file -> file.toString().replace(tempDirectory.toString() + File.separatorChar, ""))
            .collect(Collectors.toList());
    Collections.sort(packageFileList);

    FileUtils.deleteQuietly(tempDirectory.toFile());

    return packageFileList;
  }

  @Override
  public String getDownloadedFileContent(String webDriverSessionId, String downloadedFileName)
      throws IOException {
    return FileUtils.readFileToString(get(downloadDir, downloadedFileName).toFile());
  }

  @Override
  public void removeDownloadedFiles(String webDriverSessionId, String... filenames) {
    stream(filenames)
        .forEach(
            filename -> {
              try {
                deleteIfExists(get(downloadDir, filename));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
