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

package org.rapidpm.microservice.optionals.index.stores.indices;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.rapidpm.microservice.optionals.index.IndexManagement;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexOfTypeString extends BasicLuceneIndexOfType<String> {


  public static final String FIELD_TXT = "txt";


  private IndexOfTypeString(final Builder builder) {
    super(builder.indexName);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public List<String> queryByExample(final String s) {
    return Collections.emptyList();
  }

  @Override
  public List<String> query(final String query) {
    try {
      final Query q = new QueryParser(FIELD_TXT, analyzer).parse(query);
      final IndexSearcher searcher = new IndexSearcher(directoryReader);
      final TopScoreDocCollector collector = TopScoreDocCollector.create(1_00);
      searcher.search(q, collector);
      final ScoreDoc[] hits = collector.topDocs().scoreDocs;
      final List<String> result = new ArrayList<>();
      for (final ScoreDoc hit : hits) {
        result.add(hit.toString());
      }
      return result;
    } catch (ParseException | IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  @Override
  public Analyzer createAnalyzer() {
    return new StandardAnalyzer();
  }

  @Override
  protected Document transform2Document(final String value) {
    final Document doc = new Document();
    doc.add(new TextField(FIELD_TXT, value, Store.YES));
    doc.add(new StringField("created", LocalDateTime.now().format(IndexManagement.DATE_TIME_FORMATTER), Store.YES));
    return doc;
  }


  public static final class Builder {
    private String indexName;

    private Builder() {
    }

    @Nonnull
    public Builder withIndexName(@Nonnull final String indexName) {
      this.indexName = indexName;
      return this;
    }

    @Nonnull
    public IndexOfTypeString build() {
      return new IndexOfTypeString(this);
    }
  }
}
