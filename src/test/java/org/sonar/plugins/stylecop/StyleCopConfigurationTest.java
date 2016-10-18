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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.Settings;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StyleCopConfigurationTest {

  private static final String MSBUILD_EXE = new File("src/test/resources/StyleCopConfiguration/MSBuild.exe").getAbsolutePath();
  private static final String STYLECOP_DLL = new File("src/test/resources/StyleCopConfiguration/StyleCop.dll").getAbsolutePath();

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void msBuildPath() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getString("sonar.stylecop.msBuildPath")).thenReturn(MSBUILD_EXE);
    assertThat(conf.msBuildPath()).isEqualTo(MSBUILD_EXE);
  }

  @Test
  public void msBuildPath_deprecated_properties() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getString("sonar.dotnet.version")).thenReturn("4.0");
    when(settings.getString("sonar.dotnet.4.0.sdk.directory")).thenReturn(new File("src/test/resources/StyleCopConfiguration/4.0").getAbsolutePath());
    assertThat(conf.msBuildPath()).isEqualTo(new File("src/test/resources/StyleCopConfiguration/4.0/MSBuild.exe").getAbsolutePath());

    when(settings.getString("sonar.dotnet.version")).thenReturn("3.5");
    when(settings.getString("sonar.dotnet.3.5.sdk.directory")).thenReturn(new File("src/test/resources/StyleCopConfiguration/3.5").getAbsolutePath());
    assertThat(conf.msBuildPath()).isEqualTo(new File("src/test/resources/StyleCopConfiguration/3.5/MSBuild.exe").getAbsolutePath());
  }

  @Test
  public void styleCopDllPath() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getString("sonar.stylecop.styleCopDllPath")).thenReturn(STYLECOP_DLL);
    assertThat(conf.styleCopDllPath()).isEqualTo(STYLECOP_DLL);
  }

  @Test
  public void styleCopDllPath_deprecated_properties() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getString("sonar.stylecop.installDirectory")).thenReturn(new File("src/test/resources/StyleCopConfiguration").getAbsolutePath());
    assertThat(conf.styleCopDllPath()).isEqualTo(STYLECOP_DLL);
  }

  @Test
  public void projectFilePath() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getString("sonar.stylecop.projectFilePath")).thenReturn("c:/Solution/Project/project.csproj");
    assertThat(conf.projectFilePath()).isEqualTo("c:/Solution/Project/project.csproj");
  }

  @Test
  public void should_fail_with_missing_project_file_path() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("The property \"sonar.stylecop.projectFilePath\" must be set to execute StyleCop rules.");
    thrown.expectMessage("This property can be automatically set by the Analysis Bootstrapper for Visual Studio Projects plugin, see: http://docs.codehaus.org/x/TAA1Dg.");

    new StyleCopConfiguration(mock(Settings.class)).projectFilePath();
  }

  @Test
  public void timeoutMinutes() {
    Settings settings = mock(Settings.class);
    StyleCopConfiguration conf = new StyleCopConfiguration(settings);

    when(settings.getInt("sonar.stylecop.timeoutMinutes")).thenReturn(42);
    assertThat(conf.timeoutMinutes()).isEqualTo(42);
  }

  @Test
  public void should_fail_with_missing_msbuild_exe_path() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Missing the mandatory property \"sonar.stylecop.msBuildPath\".");

    new StyleCopConfiguration(mock(Settings.class)).msBuildPath();
  }

  @Test
  public void should_fail_with_invalid_msbuild_exe_path() {
    String invalid = new File("src/test/resources/StyleCopConfiguration/nonexisting.exe").getAbsolutePath();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot find the file \"" + invalid + "\" provided by the property \"sonar.stylecop.msBuildPath\".");

    Settings settings = mock(Settings.class);
    when(settings.getString("sonar.stylecop.msBuildPath")).thenReturn(invalid);
    new StyleCopConfiguration(settings).msBuildPath();
  }

  @Test
  public void should_fail_with_invalid_stylecop_dll_path() {
    String invalid = new File("src/test/resources/StyleCopConfiguration/nonexisting.dll").getAbsolutePath();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Cannot find the file \"" + invalid + "\" provided by the property \"sonar.stylecop.styleCopDllPath\".");

    Settings settings = mock(Settings.class);
    when(settings.getString("sonar.stylecop.styleCopDllPath")).thenReturn(invalid);
    new StyleCopConfiguration(settings).styleCopDllPath();
  }

  @Test
  public void ignored_hungarian_prefixes() {
    Settings settings = mock(Settings.class);
    when(settings.getString("sonar.stylecop.ignoredHungarianPrefixes")).thenReturn("");
    assertThat(new StyleCopConfiguration(settings).ignoredHungarianPrefixes()).isEmpty();
    when(settings.getString("sonar.stylecop.ignoredHungarianPrefixes")).thenReturn("  ,foo,bar  ,,");
    assertThat(new StyleCopConfiguration(settings).ignoredHungarianPrefixes()).containsOnly("foo", "bar");
  }

}
