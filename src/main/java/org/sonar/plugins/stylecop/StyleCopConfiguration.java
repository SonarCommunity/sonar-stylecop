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

import org.sonar.api.config.Settings;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class StyleCopConfiguration {

	private static final Logger LOG = Loggers.get(StyleCopConfiguration.class);

	private final Settings settings;

	public StyleCopConfiguration(Settings settings) {
		this.settings = settings;
	}

	public String msBuildPath() {
		String result;

		String netVersion = settings.getString(StyleCopPlugin.STYLECOP_OLD_DOTNET_VERSION_PROPERTY_KEY);
		if (netVersion != null) {
			String netFrameworkPropertyKey = StyleCopPlugin.STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_1 + netVersion
					+ StyleCopPlugin.STYLECOP_OLD_DOTNET_FRAMEWORK_PROPERTY_KEY_PART_2;

			logDeprecatedPropertyUsage(StyleCopPlugin.STYLECOP_MSBUILD_PATH_PROPERTY_KEY,
					StyleCopPlugin.STYLECOP_OLD_DOTNET_VERSION_PROPERTY_KEY + "\" and \"" + netFrameworkPropertyKey);

			result = mergePathAndFile(requiredProperty(netFrameworkPropertyKey), "MSBuild.exe");
		} else {
			result = requiredProperty(StyleCopPlugin.STYLECOP_MSBUILD_PATH_PROPERTY_KEY);
		}

		checkFileExists(result, StyleCopPlugin.STYLECOP_MSBUILD_PATH_PROPERTY_KEY);

		return result;
	}

	public String styleCopDllPath() {
		String result;

		String styleCopInstallDirectory = settings.getString(StyleCopPlugin.STYLECOP_OLD_INSTALL_DIRECTORY_PROPERTY_KEY);
		if (styleCopInstallDirectory != null) {
			logDeprecatedPropertyUsage(StyleCopPlugin.STYLECOP_DLL_PATH_PROPERTY_KEY,
					StyleCopPlugin.STYLECOP_OLD_INSTALL_DIRECTORY_PROPERTY_KEY);
			result = mergePathAndFile(settings.getString(StyleCopPlugin.STYLECOP_OLD_INSTALL_DIRECTORY_PROPERTY_KEY),
					"StyleCop.dll");
		} else {
			result = requiredProperty(StyleCopPlugin.STYLECOP_DLL_PATH_PROPERTY_KEY);
		}

		checkFileExists(result, StyleCopPlugin.STYLECOP_DLL_PATH_PROPERTY_KEY);

		return result;
	}

	private static void checkFileExists(String path, String property) {
		File file = new File(path);
		if (!file.isFile()) {
			throw new IllegalArgumentException(
					"Cannot find the file \"" + file.getAbsolutePath() + "\" provided by the property \"" + property + "\".");
		}
	}

	public String projectFilePath() {
		String result = settings.getString(StyleCopPlugin.STYLECOP_PROJECT_FILE_PATH_PROPERTY_KEY);
		if (result == null) {
			throw new IllegalArgumentException("The property \"" + StyleCopPlugin.STYLECOP_PROJECT_FILE_PATH_PROPERTY_KEY
					+ "\" must be set to execute StyleCop rules. "
					+ "This property can be automatically set by the Analysis Bootstrapper for Visual Studio Projects plugin, see: http://docs.codehaus.org/x/TAA1Dg.");
		}

		return result;
	}

	public int timeoutMinutes() {
		return settings.getInt(StyleCopPlugin.STYLECOP_TIMEOUT_MINUTES_PROPERTY_KEY);
	}

	public Iterable<String> ignoredHungarianPrefixes() {
		return Splitter.on(',').omitEmptyStrings().trimResults()
				.split(settings.getString(StyleCopPlugin.STYLECOP_IGNORED_HUNGARIAN_PREFIXES_PROPERTY_KEY));
	}

	private String requiredProperty(String propertyKey) {
		String value = settings.getString(propertyKey);
		Preconditions.checkArgument(value != null, "Missing the mandatory property \"" + propertyKey + "\".");
		return value;
	}

	private static String mergePathAndFile(String s1, String s2) {
		return new File(new File(s1), s2).getAbsolutePath();
	}

	private static void logDeprecatedPropertyUsage(String newPropertyKey, String oldProperty) {
		LOG.warn("Use the new property \"" + newPropertyKey + "\" instead of the deprecated \"" + oldProperty + "\".");
	}

}
