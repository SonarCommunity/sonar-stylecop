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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class StyleCopMsBuildWriterTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void test() throws Exception {
    File styleCopDllFile = tmp.newFile();
    File projectFile = tmp.newFile();
    File settingsFile = tmp.newFile();
    File reportFile = tmp.newFile();
    File file = tmp.newFile();

    new StyleCopMsBuildWriter().write(styleCopDllFile, projectFile, settingsFile, reportFile, file);
    String contents = Files.toString(file, Charsets.UTF_8);

    assertThat(contents.replace("\r", "").replace("\n", ""))
      .isEqualTo(
        "<Project xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\" DefaultTargets=\"StyleCopLaunch\">"
          + "  <UsingTask AssemblyFile=\"" + styleCopDllFile.getAbsolutePath() + "\" TaskName=\"StyleCopTask\"/>"
          + ""
          + "  <PropertyGroup>"
          + "    <FolderToAnalyse>" + projectFile.getParentFile().getAbsolutePath() + "</FolderToAnalyse>"
          + "    <ProjectPath>" + projectFile.getAbsolutePath() + "</ProjectPath>"
          + "    <StyleCopOverrideSettingsFile>" + settingsFile.getAbsolutePath() + "</StyleCopOverrideSettingsFile>"
          + "    <StyleCopOutputFile>" + reportFile.getAbsolutePath() + "</StyleCopOutputFile>"
          + "  </PropertyGroup>"
          + ""
          + "  <Target Name=\"StyleCopLaunch\">"
          + "    <CreateItem Include=\"$(FolderToAnalyse)\\**\\*.cs\">"
          + "      <Output TaskParameter=\"Include\" ItemName=\"StyleCopFiles\"/>"
          + "    </CreateItem>"
          + ""
          + "    <StyleCopTask"
          + "      ProjectFullPath=\"$(ProjectPath)\""
          + "      SourceFiles=\"@(StyleCopFiles)\""
          + "      AdditionalAddinPaths=\"\""
          + "      ForceFullAnalysis=\"true\""
          + "      DefineConstants=\"DEBUG;TRACE\""
          + "      TreatErrorsAsWarnings=\"true\""
          + "      CacheResults=\"false\""
          + "      OverrideSettingsFile=\"$(StyleCopOverrideSettingsFile)\""
          + "      OutputFile=\"$(StyleCopOutputFile)\""
          + "      MaxViolationCount=\"-1\" />"
          + "  </Target>"
          + "</Project>");
  }

}
