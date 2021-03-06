// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.skyframe;

import com.google.auto.value.AutoValue;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.analysis.TopLevelArtifactContext;
import com.google.devtools.build.skyframe.LegacySkyKey;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.util.Collection;

/**
 * A test completion value represents the completion of a test target. This includes the execution
 * of all test shards and repeated runs, if applicable.
 */
public class TestCompletionValue implements SkyValue {
  static final TestCompletionValue TEST_COMPLETION_MARKER = new TestCompletionValue();

  private TestCompletionValue() { }

  public static SkyKey key(
      ConfiguredTargetKey lac,
      final TopLevelArtifactContext topLevelArtifactContext,
      final boolean exclusiveTesting) {
    return LegacySkyKey.create(
        SkyFunctions.TEST_COMPLETION,
        TestCompletionKey.create(lac, topLevelArtifactContext, exclusiveTesting));
  }

  public static Iterable<SkyKey> keys(Collection<ConfiguredTarget> targets,
                                      final TopLevelArtifactContext topLevelArtifactContext,
                                      final boolean exclusiveTesting) {
    return Iterables.transform(
        targets,
        new Function<ConfiguredTarget, SkyKey>() {
          @Override
          public SkyKey apply(ConfiguredTarget ct) {
            return LegacySkyKey.create(
                SkyFunctions.TEST_COMPLETION,
                TestCompletionKey.create(
                    ConfiguredTargetKey.of(ct), topLevelArtifactContext, exclusiveTesting));
          }
        });
  }

  @AutoValue
  abstract static class TestCompletionKey {

    public static TestCompletionKey create(
        ConfiguredTargetKey configuredTargetKey,
        TopLevelArtifactContext topLevelArtifactContext,
        boolean exclusiveTesting) {
      return new AutoValue_TestCompletionValue_TestCompletionKey(
          configuredTargetKey, topLevelArtifactContext, exclusiveTesting);
    }

    abstract ConfiguredTargetKey configuredTargetKey();

    public abstract TopLevelArtifactContext topLevelArtifactContext();
    public abstract boolean exclusiveTesting();
  }
}
