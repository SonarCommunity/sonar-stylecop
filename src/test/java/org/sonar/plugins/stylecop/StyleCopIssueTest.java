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

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class StyleCopIssueTest {

  @Test
  public void test() {
    StyleCopIssue issue = new StyleCopIssue(0, 1, "foo.cs", "StyleCop.CSharp.MaintainabilityRules", "AccessModifierMustBeDeclared", "message1");
    assertThat(issue.reportLine()).isEqualTo(0);
    assertThat(issue.lineNumber()).isEqualTo(1);
    assertThat(issue.source()).isEqualTo("foo.cs");
    assertThat(issue.ruleNamespace()).isEqualTo("StyleCop.CSharp.MaintainabilityRules");
    assertThat(issue.rule()).isEqualTo("AccessModifierMustBeDeclared");
    assertThat(issue.message()).isEqualTo("message1");

    issue = new StyleCopIssue(42, 42, "bar.cs", "StyleCop.CSharp.DocumentationRules", "FileMustHaveHeader", "message2");
    assertThat(issue.reportLine()).isEqualTo(42);
    assertThat(issue.lineNumber()).isEqualTo(42);
    assertThat(issue.source()).isEqualTo("bar.cs");
    assertThat(issue.ruleNamespace()).isEqualTo("StyleCop.CSharp.DocumentationRules");
    assertThat(issue.rule()).isEqualTo("FileMustHaveHeader");
    assertThat(issue.message()).isEqualTo("message2");
  }

}
