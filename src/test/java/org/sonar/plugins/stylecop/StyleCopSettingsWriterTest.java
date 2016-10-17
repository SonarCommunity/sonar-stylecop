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
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

public class StyleCopSettingsWriterTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void test() throws Exception {
    File file1 = tmp.newFile();

    new StyleCopSettingsWriter().write(ImmutableList.of("foo#A", "StyleCop.CSharp.NamingRules#C", "foo#B"), ImmutableList.of("aa", "bb"), file1);
    String contents1 = Files.toString(file1, Charsets.UTF_8);

    assertThat(contents1.replace("\r", "").replace("\n", ""))
      .isEqualTo(
        "<StyleCopSettings Version=\"105\">"
          + "  <Analyzers>"
          + "    <Analyzer AnalyzerId=\"foo\">"
          + "      <Rules>"
          + "        <Rule Name=\"A\">"
          + "          <RuleSettings>"
          + "            <BooleanProperty Name=\"Enabled\">True</BooleanProperty>"
          + "          </RuleSettings>"
          + "        </Rule>"
          + "        <Rule Name=\"B\">"
          + "          <RuleSettings>"
          + "            <BooleanProperty Name=\"Enabled\">True</BooleanProperty>"
          + "          </RuleSettings>"
          + "        </Rule>"
          + "      </Rules>"
          + "    </Analyzer>"
          + "    <Analyzer AnalyzerId=\"StyleCop.CSharp.NamingRules\">"
          + "      <Rules>"
          + "        <Rule Name=\"C\">"
          + "          <RuleSettings>"
          + "            <BooleanProperty Name=\"Enabled\">True</BooleanProperty>"
          + "          </RuleSettings>"
          + "        </Rule>"
          + "      </Rules>"
          + "      <AnalyzerSettings>"
          + "        <CollectionProperty Name=\"Hungarian\">"
          + "          <Value>aa</Value>"
          + "          <Value>bb</Value>"
          + "        </CollectionProperty>"
          + "      </AnalyzerSettings>"
          + "    </Analyzer>"
          + "  </Analyzers>"
          + "</StyleCopSettings>");

    File file2 = tmp.newFile();
    new StyleCopSettingsWriter().write(ImmutableList.of("baz#SomeRuleKey"), Collections.<String>emptyList(), file2);
    String contents2 = Files.toString(file2, Charsets.UTF_8);

    assertThat(contents2)
      .contains("baz")
      .contains("SomeRuleKey")
      .doesNotContain("foo")
      .doesNotContain("CollectionProperty");
  }

}
