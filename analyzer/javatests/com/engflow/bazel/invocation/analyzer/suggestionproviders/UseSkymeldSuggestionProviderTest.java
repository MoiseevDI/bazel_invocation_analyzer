/*
 * Copyright 2023 EngFlow Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.engflow.bazel.invocation.analyzer.suggestionproviders;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.engflow.bazel.invocation.analyzer.SuggestionOutput;
import com.engflow.bazel.invocation.analyzer.dataproviders.BazelVersion;
import com.engflow.bazel.invocation.analyzer.dataproviders.SkymeldUsed;
import org.junit.Before;
import org.junit.Test;

public class UseSkymeldSuggestionProviderTest extends SuggestionProviderUnitTestBase {
  // These variables are returned from calls to DataManager.getDatum for the associated types. They
  // are set up with reasonable defaults before each test is run, but can be overridden within the
  // tests when custom values are desired for the testing being conducted (without the need to
  // re-initialize the mocking).
  private BazelVersion bazelVersion;
  private SkymeldUsed skymeldUsed;

  @Before
  public void setup() throws Exception {
    // Create reasonable defaults and set up to return the class-variables when the associated types
    // are requested.
    when(dataManager.getDatum(BazelVersion.class)).thenAnswer(i -> bazelVersion);
    when(dataManager.getDatum(SkymeldUsed.class)).thenAnswer(i -> skymeldUsed);

    suggestionProvider = new UseSkymeldSuggestionProvider();
  }

  @Test
  public void shouldReturnSuggestionForNoSkymeldUsedOnUnknownVersion() {
    bazelVersion = BazelVersion.parse(null);
    skymeldUsed = new SkymeldUsed(false);

    SuggestionOutput suggestionOutput = suggestionProvider.getSuggestions(dataManager);

    assertThat(suggestionOutput.getAnalyzerClassname())
        .isEqualTo(UseSkymeldSuggestionProvider.class.getName());
    assertThat(suggestionOutput.getSuggestionList()).hasSize(1);
    assertThat(suggestionOutput.getSuggestion(0).getCaveatCount()).isEqualTo(1);
    assertThat(suggestionOutput.hasFailure()).isFalse();
  }

  @Test
  public void shouldReturnSuggestionForNoSkymeldUsedOnKnownOldVersion() {
    bazelVersion = BazelVersion.parse("release 5.3.1");
    skymeldUsed = new SkymeldUsed(false);

    SuggestionOutput suggestionOutput = suggestionProvider.getSuggestions(dataManager);

    assertThat(suggestionOutput.getAnalyzerClassname())
        .isEqualTo(UseSkymeldSuggestionProvider.class.getName());
    assertThat(suggestionOutput.getSuggestionList()).hasSize(1);
    assertThat(suggestionOutput.getSuggestion(0).getCaveatCount()).isEqualTo(1);
    assertThat(suggestionOutput.hasFailure()).isFalse();
  }

  @Test
  public void shouldReturnSuggestionForNoSkymeldUsedOnKnownNewVersion() {
    bazelVersion = BazelVersion.parse("release 7.0.0");
    skymeldUsed = new SkymeldUsed(false);

    SuggestionOutput suggestionOutput = suggestionProvider.getSuggestions(dataManager);

    assertThat(suggestionOutput.getAnalyzerClassname())
        .isEqualTo(UseSkymeldSuggestionProvider.class.getName());
    assertThat(suggestionOutput.getSuggestionList()).hasSize(1);
    assertThat(suggestionOutput.getSuggestion(0).getCaveatList()).isEmpty();
    assertThat(suggestionOutput.hasFailure()).isFalse();
  }

  @Test
  public void shouldReturnNoSuggestionForSkymeldUsed() {
    skymeldUsed = new SkymeldUsed(true);

    SuggestionOutput suggestionOutput = suggestionProvider.getSuggestions(dataManager);

    assertThat(suggestionOutput.getAnalyzerClassname())
        .isEqualTo(UseSkymeldSuggestionProvider.class.getName());
    assertThat(suggestionOutput.getSuggestionList()).isEmpty();
    assertThat(suggestionOutput.hasFailure()).isFalse();
  }
}
