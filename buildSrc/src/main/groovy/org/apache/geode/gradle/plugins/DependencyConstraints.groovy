/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyConstraints implements Plugin<Project> {
  /** By necessity, the version of those plugins used in the build-scripts are defined in the
   * buildscript {} configuration in the root project's build.gradle. */
  static Map<String,String> disparateDependencies = initExternalDependencies()

  static String get(String name) {
    return disparateDependencies.get(name)
  }

  static private Map<String, String> initExternalDependencies() {
    Map<String,String> deps = new HashMap<>()
    // These versions are consumed beyond the scope of source set dependencies.

    // These version numbers are consumed by :geode-modules-assembly:distAppServer filtering
    // Some of these are referenced below as well
    deps.put("log4j.version", "2.14.1")
    deps.put("shiro.version", "1.7.1")

    // These version numbers are consumed by protobuf configurations that are plugin-specific and not
    // part of the typical Gradle dependency configurations.
    deps.put("protoc.version", "3.16.0")
    deps.put("protobuf-gradle-plugin.version", "0.8.16")
    deps.put("protobuf-java.version", "3.16.0")

    // These versions are referenced in test.gradle, which is aggressively injected into all projects.
    deps.put("junit.version", "4.13.2")
    deps.put("cglib.version", "3.3.0")
    return deps
  }

  @Override
  void apply(Project project) {
    def dependencySet = { Map<String, String> group_and_version, Closure closure ->
      DependencySetHandler delegate =
          new DependencySetHandler(group_and_version.get("group"), group_and_version.get("version"), project)
      closure.setDelegate(delegate)
      closure.call(delegate)
    }

    project.dependencies {
      constraints {
        // informal, inter-group dependencySet
        api(group: 'cglib', name: 'cglib', version: get('cglib.version'))
        api(group: 'com.github.stefanbirkner', name: 'system-rules', version: '1.19.0')
        api(group: 'com.google.protobuf', name: 'protobuf-gradle-plugin', version: get('protobuf-gradle-plugin.version'))
        api(group: 'com.google.protobuf', name: 'protobuf-java', version: get('protobuf-java.version'))
        api(group: 'junit', name: 'junit', version: get('junit.version'))
        api(group: 'org.apache.shiro', name: 'shiro-core', version: get('shiro.version'))
        api(group: 'org.assertj', name: 'assertj-core', version: '3.19.0')
        api(group: 'org.awaitility', name: 'awaitility', version: '4.0.3')
        api(group: 'org.mockito', name: 'mockito-core', version: '3.9.0')
      }
    }

    dependencySet(group: 'org.apache.logging.log4j', version: get('log4j.version')) {
      entry('log4j-api')
    }
  }
}
