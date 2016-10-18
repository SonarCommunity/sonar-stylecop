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

public class StyleCopIssue {

  private final int reportLine;
  private final int lineNumber;
  private final String source;
  private final String ruleNamespace;
  private final String rule;
  private final String message;

  public StyleCopIssue(int reportLine, int lineNumber, String source, String ruleNamespace, String rule, String message) {
    this.reportLine = reportLine;
    this.lineNumber = lineNumber;
    this.source = source;
    this.ruleNamespace = ruleNamespace;
    this.rule = rule;
    this.message = message;
  }

  public int reportLine() {
    return reportLine;
  }

  public int lineNumber() {
    return lineNumber;
  }

  public String source() {
    return source;
  }

  public String ruleNamespace() {
    return ruleNamespace;
  }

  public String rule() {
    return rule;
  }

  public String message() {
    return message;
  }

}
