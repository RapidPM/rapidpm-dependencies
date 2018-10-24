/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reflections.scanners;

import org.reflections.Configuration;
import org.reflections.ReflectionsException;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.vfs.Vfs;
import repacked.com.google.common.base.Predicate;
import repacked.com.google.common.base.Predicates;
import repacked.com.google.common.collect.Multimap;

/**
 *
 */
@SuppressWarnings({"RawUseOfParameterizedType" , "unchecked"})
public abstract class AbstractScanner implements Scanner {

  private Configuration            configuration;
  private Multimap<String, String> store;
  private Predicate<String>        resultFilter = Predicates.alwaysTrue(); //accept all by default

  //
  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
  }

  public Multimap<String, String> getStore() {
    return store;
  }

  public void setStore(final Multimap<String, String> store) {
    this.store = store;
  }

  public Scanner filterResultsBy(Predicate<String> filter) {
    this.setResultFilter(filter);
    return this;
  }

  public boolean acceptsInput(String file) {
    return getMetadataAdapter().acceptsInput(file);
  }

  public Object scan(Vfs.File file , Object classObject) {
    if (classObject == null) {
      try {
        classObject = configuration.getMetadataAdapter().getOfCreateClassObject(file);
      } catch (Exception e) {
        throw new ReflectionsException("could not create class object from file " + file.getRelativePath() , e);
      }
    }
    scan(classObject);
    return classObject;
  }

  public abstract void scan(Object cls);

  //
  public boolean acceptResult(final String fqn) {
    return fqn != null && resultFilter.apply(fqn);
  }

  protected MetadataAdapter getMetadataAdapter() {
    return configuration.getMetadataAdapter();
  }

  public Predicate<String> getResultFilter() {
    return resultFilter;
  }

  public void setResultFilter(Predicate<String> resultFilter) {
    this.resultFilter = resultFilter;
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  //
  @Override
  public boolean equals(Object o) {
    return this == o || o != null && getClass() == o.getClass();
  }
}
