/*
 * SonarQube StyleCop Plugin
 * Copyright (C) 2014-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.stylecop;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputPath;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;

public class FileProvider {

  private final Project project;
  private final SensorContext context;
  
  private static final Logger LOG = Loggers.get(FileProvider.class);

  public FileProvider(Project project, SensorContext context) {
    this.project = project;
    this.context = context;
  }

  public org.sonar.api.resources.File fromIOFile(File file) {
    // Workaround SonarQube < 4.2, the context should not be required
  	FileSystem fs = context.fileSystem();
  	InputFile inFile = fs.inputFile(fs.predicates().is(file));
  	
  	if(inFile == null)
  	{
  		LOG.debug("Base dir: {}", fs.baseDir());
  		LOG.debug("FileProvider for {} returns an InputFile that is {}", file.getAbsolutePath(), inFile);
  	}

  	return inFile == null ? null : (org.sonar.api.resources.File) context.getResource(inFile);
  }

}
