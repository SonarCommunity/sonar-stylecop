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

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class StyleCopReportParserTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void valid() {
    List<StyleCopIssue> issues = new StyleCopReportParser().parse(new File("src/test/resources/StyleCopReportParserTest/valid.xml"));

    assertThat(issues).hasSize(7);

    StyleCopIssue issue = issues.get(0);
    assertThat(issue.reportLine()).isEqualTo(2);
    assertThat(issue.lineNumber()).isEqualTo(1);
    assertThat(issue.source()).isEqualTo("MyLibrary\\Program.cs");
    assertThat(issue.ruleNamespace()).isEqualTo("StyleCop.CSharp.DocumentationRules");
    assertThat(issue.rule()).isEqualTo("FileMustHaveHeader");
    assertThat(issue.message()).isEqualTo("The file has no header, the header Xml is invalid, or the header is not located at the top of the file.");

    issue = issues.get(2);
    assertThat(issue.reportLine()).isEqualTo(4);
    assertThat(issue.lineNumber()).isEqualTo(11);
    assertThat(issue.source()).isEqualTo("MyLibrary\\Program.cs");
    assertThat(issue.ruleNamespace()).isEqualTo("StyleCop.CSharp.MaintainabilityRules");
    assertThat(issue.rule()).isEqualTo("AccessModifierMustBeDeclared");
    assertThat(issue.message()).isEqualTo("The method must have an access modifier.");

    issue = issues.get(3);
    assertThat(issue.reportLine()).isEqualTo(5);
    assertThat(issue.lineNumber()).isEqualTo(1);
    assertThat(issue.source()).isEqualTo("MyLibrary\\obj\\Debug\\TemporaryGeneratedFile_036C0B5B-1481-4323-8D20-8F5ADCB23D92.cs");
    assertThat(issue.ruleNamespace()).isEqualTo("StyleCop.CSharp.DocumentationRules");
    assertThat(issue.rule()).isEqualTo("FileMustHaveHeader");
    assertThat(issue.message()).isEqualTo("The file has no header, the header Xml is invalid, or the header is not located at the top of the file.");
  }

  @Test
  public void invalid_line() {
    thrown.expectMessage("Expected an integer instead of \"foo\" for the attribute \"LineNumber\"");
    thrown.expectMessage("invalid_line.xml at line 2");

    new StyleCopReportParser().parse(new File("src/test/resources/StyleCopReportParserTest/invalid_line.xml"));
  }

  @Test
  public void missing_source() {
    thrown.expectMessage("Missing attribute \"Source\" in element <Violation>");
    thrown.expectMessage("missing_source.xml at line 2");

    new StyleCopReportParser().parse(new File("src/test/resources/StyleCopReportParserTest/missing_source.xml"));
  }

  @Test
  public void non_existing() {
    thrown.expectMessage("java.io.FileNotFoundException");
    thrown.expectMessage("non_existing.xml");

    new StyleCopReportParser().parse(new File("src/test/resources/StyleCopReportParserTest/non_existing.xml"));
  }

}
