/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shadowmask.core.domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.shadowmask.core.domain.tree.LongTaxTree;

public class TestDomainTreeBuiltFromSortedValues {
  @Test public void test() {
    LongTaxTree tree = new LongTaxTree();
    List<Long> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      list.add((long) i);
    }
    tree.buildFromSortedValues(list.iterator(),10,5,new int[]{4,3,2});
    Assert.assertEquals(5,tree.getLeaves().size());
  }
}
