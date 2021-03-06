/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package junit.org.rapidpm.microservice.optionals.index.stores.v002;

import junit.org.rapidpm.microservice.optionals.index.stores.IndexStoreBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.microservice.optionals.index.stores.indices.IndexOfType;

import java.util.List;
import java.util.Set;

public class IndexStoreTest extends IndexStoreBaseTest {


  public static final String TESTINDEX = IndexStoreTest.class.getSimpleName();


  @Test
  public void indexElement() throws Exception {
    Assert.assertTrue(indexStore.containsIndex(TESTINDEX));
    Assert.assertFalse(indexStore.containsIndex("NOOP"));

    final Set<String> indexNameSet = indexStore.getIndexNameSet();
    Assert.assertNotNull(indexNameSet);
    Assert.assertFalse(indexNameSet.isEmpty());
//    Assert.assertEquals(1, indexNameSet.size());

    final IndexOfType<String> index = indexStore.getIndex(TESTINDEX);
    Assert.assertNotNull(index);

    index.addElement("Hallo Nase Trollpopel");
    final List<String> hallo = index.query("Trollpopel");
    Assert.assertNotNull(hallo);

    Assert.assertFalse(hallo.isEmpty());
  }

  @Override
  public String getTestIndexName() {
    return TESTINDEX;
  }
}