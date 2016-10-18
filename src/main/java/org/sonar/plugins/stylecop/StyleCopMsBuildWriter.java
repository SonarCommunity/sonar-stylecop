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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class StyleCopMsBuildWriter {

  public void write(File styleCopDllFile, File projectFile, File settingsFile, File reportFile, File file) {
    StringBuilder sb = new StringBuilder();

    appendLine(sb, "<Project xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\" DefaultTargets=\"StyleCopLaunch\">");
    appendLine(sb, "  <UsingTask AssemblyFile=\"" + styleCopDllFile.getAbsolutePath() + "\" TaskName=\"StyleCopTask\"/>");
    appendLine(sb, "");

    appendLine(sb, "  <PropertyGroup>");
    appendLine(sb, "    <FolderToAnalyse>" + projectFile.getParentFile().getAbsolutePath() + "</FolderToAnalyse>");
    appendLine(sb, "    <ProjectPath>" + projectFile.getAbsolutePath() + "</ProjectPath>");
    appendLine(sb, "    <StyleCopOverrideSettingsFile>" + settingsFile.getAbsolutePath() + "</StyleCopOverrideSettingsFile>");
    appendLine(sb, "    <StyleCopOutputFile>" + reportFile.getAbsolutePath() + "</StyleCopOutputFile>");
    appendLine(sb, "  </PropertyGroup>");
    appendLine(sb, "");

    appendLine(sb, "  <Target Name=\"StyleCopLaunch\">");
    appendLine(sb, "    <CreateItem Include=\"$(FolderToAnalyse)\\**\\*.cs\">");
    appendLine(sb, "      <Output TaskParameter=\"Include\" ItemName=\"StyleCopFiles\"/>");
    appendLine(sb, "    </CreateItem>");
    appendLine(sb, "");

    appendLine(sb, "    <StyleCopTask");
    appendLine(sb, "      ProjectFullPath=\"$(ProjectPath)\"");
    appendLine(sb, "      SourceFiles=\"@(StyleCopFiles)\"");
    appendLine(sb, "      AdditionalAddinPaths=\"\"");
    appendLine(sb, "      ForceFullAnalysis=\"true\"");
    appendLine(sb, "      DefineConstants=\"DEBUG;TRACE\"");
    appendLine(sb, "      TreatErrorsAsWarnings=\"true\"");
    appendLine(sb, "      CacheResults=\"false\"");
    appendLine(sb, "      OverrideSettingsFile=\"$(StyleCopOverrideSettingsFile)\"");
    appendLine(sb, "      OutputFile=\"$(StyleCopOutputFile)\"");
    appendLine(sb, "      MaxViolationCount=\"-1\" />");
    appendLine(sb, "  </Target>");
    appendLine(sb, "</Project>");

    try {
      Files.write(sb.toString().getBytes(Charsets.UTF_8), file);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private static void appendLine(StringBuilder sb, String s) {
    sb.append(s);
    sb.append(IOUtils.LINE_SEPARATOR);
  }

}
