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
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class StyleCopSettingsWriter {

  public void write(List<String> ruleConfigKeys, Iterable<String> ignoredHungarianPrefixes, File file) {
    StringBuilder sb = new StringBuilder();

    appendLine(sb, "<StyleCopSettings Version=\"105\">");
    appendLine(sb, "  <Analyzers>");

    for (String ruleNamespace : ruleNamespaces(ruleConfigKeys)) {
      appendLine(sb, "    <Analyzer AnalyzerId=\"" + ruleNamespace + "\">");
      appendLine(sb, "      <Rules>");
      for (String ruleKey : ruleKeys(ruleNamespace, ruleConfigKeys)) {
        appendLine(sb, "        <Rule Name=\"" + ruleKey + "\">");
        appendLine(sb, "          <RuleSettings>");
        appendLine(sb, "            <BooleanProperty Name=\"Enabled\">True</BooleanProperty>");
        appendLine(sb, "          </RuleSettings>");
        appendLine(sb, "        </Rule>");
      }
      appendLine(sb, "      </Rules>");
      if ("StyleCop.CSharp.NamingRules".equals(ruleNamespace)) {
        appendLine(sb, "      <AnalyzerSettings>");
        appendLine(sb, "        <CollectionProperty Name=\"Hungarian\">");
        for (String ignoredHungarianPrefix : ignoredHungarianPrefixes) {
          appendLine(sb, "          <Value>" + ignoredHungarianPrefix + "</Value>");
        }
        appendLine(sb, "        </CollectionProperty>");
        appendLine(sb, "      </AnalyzerSettings>");
      }
      appendLine(sb, "    </Analyzer>");
    }

    appendLine(sb, "  </Analyzers>");
    appendLine(sb, "</StyleCopSettings>");

    try {
      Files.write(sb.toString().getBytes(Charsets.UTF_8), file);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  private static List<String> ruleNamespaces(List<String> ruleConfigKeys) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    Set<String> alreadyAddedNamespaces = Sets.newHashSet();

    for (String ruleConfigKey : ruleConfigKeys) {
      String ruleNamespace = ruleConfigKey.substring(0, ruleConfigKey.indexOf('#'));
      if (!alreadyAddedNamespaces.contains(ruleNamespace)) {
        builder.add(ruleNamespace);
        alreadyAddedNamespaces.add(ruleNamespace);
      }
    }

    return builder.build();
  }

  private static List<String> ruleKeys(String ruleNamespace, List<String> ruleConfigKeys) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    for (String ruleConfigKey : ruleConfigKeys) {
      if (ruleConfigKey.startsWith(ruleNamespace)) {
        String ruleKey = ruleConfigKey.substring(ruleNamespace.length() + 1);
        builder.add(ruleKey);
      }
    }

    return builder.build();
  }

  private static void appendLine(StringBuilder sb, String s) {
    sb.append(s);
    sb.append(IOUtils.LINE_SEPARATOR);
  }

}
