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

import com.google.common.collect.ImmutableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.issue.Issue;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StyleCopSensorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldExecuteOnProject() {
    Settings settings = mock(Settings.class);
    RulesProfile profile = mock(RulesProfile.class);
    ModuleFileSystem fileSystem = mock(ModuleFileSystem.class);
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);

    Project project = mock(Project.class);

    StyleCopSensor sensor = new StyleCopSensor(settings, profile, fileSystem, perspectives);

    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.<File>of());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.of(mock(File.class)));
    when(profile.getActiveRulesByRepository("stylecop")).thenReturn(ImmutableList.<ActiveRule>of());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.of(mock(File.class)));
    when(profile.getActiveRulesByRepository("stylecop")).thenReturn(ImmutableList.of(mock(ActiveRule.class)));
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void analyze() throws Exception {
    StyleCopConfiguration styleCopConf = mockStyleCopConf("MSBuild.exe", "StyleCop.exe", "MyProject.csproj", 60);
    RulesProfile profile = mock(RulesProfile.class);
    ModuleFileSystem fileSystem = mock(ModuleFileSystem.class);
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);

    StyleCopSensor sensor = new StyleCopSensor(mock(Settings.class), profile, fileSystem, perspectives);

    List<ActiveRule> activeRules = mockActiveRules("AccessModifierMustBeDeclared", "AccessibleFieldsMustBeginWithUpperCaseLetter");
    when(profile.getActiveRulesByRepository("stylecop")).thenReturn(activeRules);

    SensorContext context = mock(SensorContext.class);
    FileProvider fileProvider = mock(FileProvider.class);
    StyleCopExecutor executor = mock(StyleCopExecutor.class);

    File workingDir = new File("target/StyleCopSensorTest/working-dir");
    when(fileSystem.workingDir()).thenReturn(workingDir);

    org.sonar.api.resources.File fooSonarFileWithIssuable = mockSonarFile("foo");
    org.sonar.api.resources.File fooSonarFileWithoutIssuable = mockSonarFile("foo");
    org.sonar.api.resources.File barSonarFile = mockSonarFile("bar");

    when(fileProvider.fromIOFile(new File("Class1.cs"))).thenReturn(null);
    when(fileProvider.fromIOFile(new File("Class2.cs"))).thenReturn(fooSonarFileWithIssuable);
    when(fileProvider.fromIOFile(new File("Class3.cs"))).thenReturn(fooSonarFileWithIssuable);
    when(fileProvider.fromIOFile(new File("Class4.cs"))).thenReturn(fooSonarFileWithoutIssuable);
    when(fileProvider.fromIOFile(new File("Class5.cs"))).thenReturn(barSonarFile);
    when(fileProvider.fromIOFile(new File("Class6.cs"))).thenReturn(fooSonarFileWithIssuable);

    Issue issue1 = mock(Issue.class);
    IssueBuilder issueBuilder1 = mockIssueBuilder();
    when(issueBuilder1.build()).thenReturn(issue1);

    Issue issue2 = mock(Issue.class);
    IssueBuilder issueBuilder2 = mockIssueBuilder();
    when(issueBuilder2.build()).thenReturn(issue2);

    Issuable issuable = mock(Issuable.class);
    when(perspectives.as(Issuable.class, fooSonarFileWithIssuable)).thenReturn(issuable);
    when(issuable.newIssueBuilder()).thenReturn(issueBuilder1, issueBuilder2);

    StyleCopSettingsWriter settingsWriter = mock(StyleCopSettingsWriter.class);
    StyleCopMsBuildWriter msBuildWriter = mock(StyleCopMsBuildWriter.class);

    StyleCopReportParser parser = mock(StyleCopReportParser.class);
    when(parser.parse(new File(workingDir, "StyleCop-report.xml"))).thenReturn(
      ImmutableList.of(
        new StyleCopIssue(100, 1, "Class1.cs", "MyNamespace", "AccessModifierMustBeDeclared", "First message"),
        new StyleCopIssue(200, 2, "Class2.cs", "MyNamespace", "AccessModifierMustBeDeclared", "Second message"),
        new StyleCopIssue(300, 3, "Class3.cs", "MyNamespace", "AccessibleFieldsMustBeginWithUpperCaseLetter", "Third message"),
        new StyleCopIssue(400, 4, "Class4.cs", "MyNamespace", "AccessModifierMustBeDeclared", "Fourth message"),
        new StyleCopIssue(500, 5, "Class5.cs", "MyNamespace", "AccessModifierMustBeDeclared", "Fifth message"),
        new StyleCopIssue(600, 6, "Class6.cs", "MyNamespace", "NonExistingRule", "Sixth message")));

    sensor.analyse(context, fileProvider, styleCopConf, settingsWriter, msBuildWriter, parser, executor);

    verify(settingsWriter).write(
      ImmutableList.of("MyNamespace#AccessModifierMustBeDeclared", "MyNamespace#AccessibleFieldsMustBeginWithUpperCaseLetter"),
      Collections.<String>emptyList(),
      new File(workingDir, "StyleCop-settings.StyleCop"));
    verify(executor).execute(
      "MSBuild.exe", new File(workingDir, "StyleCop-msbuild.proj").getAbsolutePath(), 60,
      "StyleCop's execution timed out. Increase the timeout by setting \"sonar.stylecop.timeoutMinutes\" property.");

    verify(issuable).addIssue(issue1);
    verify(issuable).addIssue(issue2);

    verify(issueBuilder1).line(2);
    verify(issueBuilder1).message("Second message");

    verify(issueBuilder2).line(3);
    verify(issueBuilder2).message("Third message");
  }

  private static org.sonar.api.resources.File mockSonarFile(String languageKey) {
    Language language = mock(Language.class);
    when(language.getKey()).thenReturn(languageKey);
    org.sonar.api.resources.File sonarFile = mock(org.sonar.api.resources.File.class);
    when(sonarFile.getLanguage()).thenReturn(language);
    return sonarFile;
  }

  private static IssueBuilder mockIssueBuilder() {
    IssueBuilder issueBuilder = mock(IssueBuilder.class);
    when(issueBuilder.ruleKey(Mockito.any(RuleKey.class))).thenReturn(issueBuilder);
    when(issueBuilder.line(Mockito.anyInt())).thenReturn(issueBuilder);
    when(issueBuilder.message(Mockito.anyString())).thenReturn(issueBuilder);
    return issueBuilder;
  }

  private static List<ActiveRule> mockActiveRules(String... activeRuleKeys) {
    ImmutableList.Builder<ActiveRule> builder = ImmutableList.builder();
    for (String activeRuleKey : activeRuleKeys) {
      ActiveRule activeRule = mock(ActiveRule.class);
      when(activeRule.getRuleKey()).thenReturn(activeRuleKey);
      when(activeRule.getConfigKey()).thenReturn("MyNamespace#" + activeRuleKey);
      builder.add(activeRule);
    }
    return builder.build();
  }

  private static StyleCopConfiguration mockStyleCopConf(String msBuildPath, String styleCopDllPath, String projectFilePath, int timeoutMinutes) {
    StyleCopConfiguration styleCopConf = mock(StyleCopConfiguration.class);
    when(styleCopConf.msBuildPath()).thenReturn(msBuildPath);
    when(styleCopConf.styleCopDllPath()).thenReturn(styleCopDllPath);
    when(styleCopConf.projectFilePath()).thenReturn(projectFilePath);
    when(styleCopConf.timeoutMinutes()).thenReturn(timeoutMinutes);
    when(styleCopConf.ignoredHungarianPrefixes()).thenReturn(Collections.<String>emptyList());
    return styleCopConf;
  }

}
