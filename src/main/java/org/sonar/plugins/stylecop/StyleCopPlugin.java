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
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import javax.annotation.Nullable;

import java.util.List;

public class StyleCopPlugin extends SonarPlugin {

  public static final String LANGUAGE_KEY = "cs";
  public static final String REPOSITORY_KEY = "stylecop";

  public static final String STYLECOP_MSBUILD_PATH_PROPERTY_KEY = "sonar.stylecop.msBuildPath";
  public static final String STYLECOP_DLL_PATH_PROPERTY_KEY = "sonar.stylecop.styleCopDllPath";
  public static final String STYLECOP_PROJECT_FILE_PATH_PROPERTY_KEY = "sonar.stylecop.projectFilePath";
  public static final String STYLECOP_TIMEOUT_MINUTES_PROPERTY_KEY = "sonar.stylecop.timeoutMinutes";
  public static final String STYLECOP_IGNORED_HUNGARIAN_PREFIXES_PROPERTY_KEY = "sonar.stylecop.ignoredHungarianPrefixes";

  public static final String STYLECOP_OLD_INSTALL_DIRECTORY_PROPERTY_KEY = "sonar.stylecop.installDirectory";
  public static final String STYLECOP_OLD_DOTNET_VERSION_PROPERTY_KEY = "sonar.dotnet.version";
  public static final String STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 = "sonar.dotnet.";
  public static final String STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2 = ".sdk.directory";

  private static final String CATEGORY = "C#";
  private static final String SUBCATEGORY = "StyleCop";

  @Override
  public List getExtensions() {
    return ImmutableList.of(
      StyleCopRuleRepository.class,
      StyleCopSensor.class,

      PropertyDefinition.builder(STYLECOP_MSBUILD_PATH_PROPERTY_KEY)
        .name("Path to MsBuild.exe")
        .description("Example: C:/Windows/Microsoft.NET/Framework/v4.0.30319/MSBuild.exe")
        .defaultValue("C:/Windows/Microsoft.NET/Framework/v4.0.30319/MSBuild.exe")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(STYLECOP_DLL_PATH_PROPERTY_KEY)
        .name("Path to StyleCop.dll")
        .description("Example: C:/Program Files (x86)/StyleCop 4.7/StyleCop.dll")
        .defaultValue("C:/Program Files (x86)/StyleCop 4.7/StyleCop.dll")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(STYLECOP_PROJECT_FILE_PATH_PROPERTY_KEY)
        .name("Project file")
        .description("Example: C:/Users/MyUser/Documents/Visual Studio 2013/Projects/MyProject/Project1/Project1.csproj")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(STYLECOP_TIMEOUT_MINUTES_PROPERTY_KEY)
        .name("Timeout in minutes")
        .description("Example: 60 for a one hour timeout")
        .defaultValue("60")
        .type(PropertyType.INTEGER)
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(STYLECOP_IGNORED_HUNGARIAN_PREFIXES_PROPERTY_KEY)
        .name("Ignored Hungarian prefixes")
        .description("Comma-separated list of prefixes to ignore for the Hungarian naming rules.<br />Example: as,is,do")
        .defaultValue("as,do,id,if,in,is,my,no,on,to,ui")
        .category(CATEGORY)
        .subCategory(SUBCATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),

      deprecatedPropertyDefinition(STYLECOP_OLD_INSTALL_DIRECTORY_PROPERTY_KEY),

      deprecatedPropertyDefinition(STYLECOP_OLD_DOTNET_VERSION_PROPERTY_KEY),

      deprecatedPropertyDefinition(
        STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 + "2.0" + STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2,
        "C:/WINDOWS/Microsoft.NET/Framework/v2.0.50727"),
      deprecatedPropertyDefinition(
        STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 + "3.5" + STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2,
        "C:/WINDOWS/Microsoft.NET/Framework/v3.5"),
      deprecatedPropertyDefinition(
        STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 + "4.0" + STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2,
        "C:/WINDOWS/Microsoft.NET/Framework/v4.0.30319"),
      deprecatedPropertyDefinition(
        STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 + "4.5" + STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2,
        "C:/WINDOWS/Microsoft.NET/Framework/v4.0.30319"));
  }

  private static PropertyDefinition deprecatedPropertyDefinition(String oldKey) {
    return deprecatedPropertyDefinition(oldKey, null);
  }

  private static PropertyDefinition deprecatedPropertyDefinition(String oldKey, @Nullable String defaultValue) {
    PropertyDefinition.Builder builder = PropertyDefinition
      .builder(oldKey)
      .name(oldKey)
      .description("This property is deprecated and will be removed in a future version.<br />"
        + "You should stop using it as soon as possible.<br />"
        + "Consult the migration guide for guidance.")
      .category(CATEGORY)
      .subCategory("Deprecated")
      .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE);

    if (defaultValue != null) {
      builder.defaultValue(defaultValue);
    }

    return builder.build();
  }

}
