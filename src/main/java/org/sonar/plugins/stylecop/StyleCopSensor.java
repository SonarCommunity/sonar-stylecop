/*
 * SonarQube StyleCop Plugin
 * Copyright (C) 2014 SonarSource
 * dev@sonar.codehaus.org
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.stylecop;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.util.List;
import java.util.Set;

public class StyleCopSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(StyleCopSensor.class);

  private final Settings settings;
  private final RulesProfile profile;
  private final ModuleFileSystem fileSystem;
  private final ResourcePerspectives perspectives;

  public StyleCopSensor(Settings settings, RulesProfile profile, ModuleFileSystem fileSystem, ResourcePerspectives perspectives) {
    this.settings = settings;
    this.profile = profile;
    this.fileSystem = fileSystem;
    this.perspectives = perspectives;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    boolean shouldExecute;

    if (!hasFilesToAnalyze()) {
      shouldExecute = false;
    } else if (profile.getActiveRulesByRepository(StyleCopPlugin.REPOSITORY_KEY).isEmpty()) {
      LOG.info("All StyleCop rules are disabled, skipping its execution.");
      shouldExecute = false;
    } else {
      shouldExecute = true;
    }

    return shouldExecute;
  }

  private boolean hasFilesToAnalyze() {
    return !fileSystem.files(FileQuery.onSource().onLanguage(StyleCopPlugin.LANGUAGE_KEY)).isEmpty();
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    analyse(context, new FileProvider(project, context), new StyleCopSettingsWriter(), new StyleCopMsBuildWriter(), new StyleCopReportParser(), new StyleCopExecutor());
  }

  @VisibleForTesting
  void analyse(SensorContext context, FileProvider fileProvider, StyleCopSettingsWriter settingsWriter, StyleCopMsBuildWriter msBuildWriter, StyleCopReportParser parser,
    StyleCopExecutor executor) {

    File settingsFile = new File(fileSystem.workingDir(), "StyleCop-settings.StyleCop");
    settingsWriter.write(enabledRuleConfigKeys(), settingsFile);

    File msBuildFile = new File(fileSystem.workingDir(), "StyleCop-msbuild.proj");
    File reportFile = new File(fileSystem.workingDir(), "stylecop-report.xml");
    msBuildWriter.write(
      new File("C:/Program Files/StyleCop 4.7/StyleCop.dll"),
      new File("C:/Users/SonarSource/Documents/Visual Studio 2013/Projects/CSharpPlayground/MyLibrary/MyLibrary.csproj"),
      settingsFile, reportFile, msBuildFile); // FIXME

    executor.execute(settings.getString("sonar.stylecop.dll.path"), msBuildFile.getAbsolutePath());

    Set<String> enabledRuleKeys = enabledRuleKeys();
    for (StyleCopIssue issue : parser.parse(reportFile)) {
      File file = new File(issue.source());
      org.sonar.api.resources.File sonarFile = fileProvider.fromIOFile(file);
      if (sonarFile == null) {
        logSkippedIssueOutsideOfSonarQube(issue, file);
      } else {
        Issuable issuable = perspectives.as(Issuable.class, sonarFile);
        if (issuable == null) {
          logSkippedIssueOutsideOfSonarQube(issue, file);
        } else if (!enabledRuleKeys.contains(issue.rule())) {
          logSkippedIssue(issue, "because the rule \"" + issue.rule() + "\" is either missing or inactive in the quality profile.");
        } else {
          issuable.addIssue(
            issuable.newIssueBuilder()
              .ruleKey(RuleKey.of(StyleCopPlugin.REPOSITORY_KEY, issue.rule()))
              .line(issue.lineNumber())
              .message(issue.message())
              .build());
        }
      }
    }
  }

  private static void logSkippedIssueOutsideOfSonarQube(StyleCopIssue issue, File file) {
    logSkippedIssue(issue, "whose file \"" + file.getAbsolutePath() + "\" is not in SonarQube.");
  }

  private static void logSkippedIssue(StyleCopIssue issue, String reason) {
    LOG.info("Skipping the StyleCop issue at line " + issue.reportLine() + " " + reason);
  }

  private List<String> enabledRuleConfigKeys() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (ActiveRule activeRule : profile.getActiveRulesByRepository(StyleCopPlugin.REPOSITORY_KEY)) {
      builder.add(activeRule.getConfigKey());
    }
    return builder.build();
  }

  private Set<String> enabledRuleKeys() {
    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
    for (ActiveRule activeRule : profile.getActiveRulesByRepository(StyleCopPlugin.REPOSITORY_KEY)) {
      builder.add(activeRule.getRuleKey());
    }
    return builder.build();
  }

}
