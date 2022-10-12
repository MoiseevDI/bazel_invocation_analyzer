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

import com.engflow.bazel.invocation.analyzer.core.Datum;
import com.engflow.bazel.invocation.analyzer.time.DurationUtil;
import java.time.Duration;
import java.util.List;

/**
 * This class collects all high-level aggregations of action level metrics:
 * <li>Bottlenecks: defined as intervals of a build during which the action count is constantly
 *     lower than the core count, which is theoretically the highest possible action count as well
 *     as the optimal one the build should always be running at.
 */
public class ActionStats implements Datum {
  public final List<Bottleneck> bottlenecks;

  public ActionStats(List<Bottleneck> bottlenecks) {
    this.bottlenecks = bottlenecks;
  }

  @Override
  public String getDescription() {
    return "A list of bottlenecks, each of which include timing information and a list of actions"
        + " that prevent more parallelization. Extracted from the Bazel profile.";
  }

  @Override
  public String getSummary() {
    var duration = bottlenecks.stream().map(b -> b.getDuration()).reduce(Duration::plus);
    if (bottlenecks.size() > 0 && duration.isPresent()) {
      return String.format(
          "%d bottlenecks found for a total duration of %s.",
          bottlenecks.size(), DurationUtil.formatDuration(duration.get()));
    }
    return null;
  }
}