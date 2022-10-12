/*
 * Copyright 2022 EngFlow Inc.
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

package com.engflow.bazel.invocation.analyzer.dataproviders;

import static com.google.common.truth.Truth.assertThat;

import com.engflow.bazel.invocation.analyzer.bazelprofile.BazelProfilePhase;
import com.engflow.bazel.invocation.analyzer.time.Timestamp;
import org.junit.Test;

public class BazelPhaseDescriptionsTest {
  @Test
  public void getOrClosestBeforeShouldReturnSelf() {
    BazelPhaseDescription description =
        new BazelPhaseDescription(Timestamp.ofMicros(1), Timestamp.ofMicros(2));
    BazelPhaseDescriptions descriptions =
        BazelPhaseDescriptions.newBuilder().add(BazelProfilePhase.EVALUATE, description).build();
    assertThat(descriptions.getOrClosestBefore(BazelProfilePhase.EVALUATE)).isEqualTo(description);
  }

  @Test
  public void getOrClosestBeforeShouldReturnEarlier() {
    BazelPhaseDescription expectedDescription =
        new BazelPhaseDescription(Timestamp.ofMicros(1), Timestamp.ofMicros(2));
    BazelPhaseDescription otherDescription =
        new BazelPhaseDescription(Timestamp.ofMicros(5), Timestamp.ofMicros(8));
    BazelPhaseDescriptions descriptions =
        BazelPhaseDescriptions.newBuilder()
            .add(BazelProfilePhase.INIT, otherDescription)
            .add(BazelProfilePhase.EVALUATE, expectedDescription)
            .add(BazelProfilePhase.PREPARE, otherDescription)
            .build();
    assertThat(descriptions.getOrClosestBefore(BazelProfilePhase.DEPENDENCIES))
        .isEqualTo(expectedDescription);
  }

  @Test
  public void getOrClosestBeforeShouldReturnNull() {
    BazelPhaseDescriptions descriptions = BazelPhaseDescriptions.newBuilder().build();
    assertThat(descriptions.getOrClosestBefore(BazelProfilePhase.EVALUATE)).isNull();
  }

  @Test
  public void getOrClosestAfterShouldReturnSelf() {
    BazelPhaseDescription description =
        new BazelPhaseDescription(Timestamp.ofMicros(1), Timestamp.ofMicros(2));
    BazelPhaseDescriptions descriptions =
        BazelPhaseDescriptions.newBuilder().add(BazelProfilePhase.EVALUATE, description).build();
    assertThat(descriptions.getOrClosestAfter(BazelProfilePhase.EVALUATE)).isEqualTo(description);
  }

  @Test
  public void getOrClosestAfterShouldReturnLater() {
    BazelPhaseDescription expectedDescription =
        new BazelPhaseDescription(Timestamp.ofMicros(1), Timestamp.ofMicros(2));
    BazelPhaseDescription otherDescription =
        new BazelPhaseDescription(Timestamp.ofMicros(5), Timestamp.ofMicros(8));
    BazelPhaseDescriptions descriptions =
        BazelPhaseDescriptions.newBuilder()
            .add(BazelProfilePhase.INIT, otherDescription)
            .add(BazelProfilePhase.PREPARE, expectedDescription)
            .add(BazelProfilePhase.EXECUTE, otherDescription)
            .build();
    assertThat(descriptions.getOrClosestAfter(BazelProfilePhase.EVALUATE))
        .isEqualTo(expectedDescription);
  }

  @Test
  public void getOrClosestAfterShouldReturnNull() {
    BazelPhaseDescriptions descriptions = BazelPhaseDescriptions.newBuilder().build();
    assertThat(descriptions.getOrClosestAfter(BazelProfilePhase.EVALUATE)).isNull();
  }
}