/*
 * SonarSource :: StyleCop :: ITs :: Plugin
 * Copyright (C) 2014 SonarSource
 * sonarqube@googlegroups.com
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
package com.sonar.it.stylecop;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.OrchestratorBuilder;
import com.sonar.orchestrator.build.SonarRunner;
import com.sonar.orchestrator.locator.FileLocation;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  IgnoredHungarianPrefixesTest.class,
  IssueTest.class,
  OldInstallDirectoryPropertyTest.class,
  OldNetFrameworkPropertyTest.class,
  TimeoutTest.class
})
public class Tests {

  @ClassRule
  public static final Orchestrator ORCHESTRATOR;

  static {
    OrchestratorBuilder orchestratorBuilder = Orchestrator.builderEnv()
      .addPlugin(FileLocation.of("../../target/sonar-stylecop-plugin.jar"))
      .addPlugin("csharp")
      .restoreProfileAtStartup(FileLocation.of("profiles/profile.xml"))
      .restoreProfileAtStartup(FileLocation.of("profiles/hungarian_rule.xml"));
    ORCHESTRATOR = orchestratorBuilder.build();
  }

  public static SonarRunner createSonarRunner() {
    return SonarRunner.create();
  }

}
