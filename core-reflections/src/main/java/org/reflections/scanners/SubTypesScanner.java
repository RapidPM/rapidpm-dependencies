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

import java.util.List;

import org.reflections.util.FilterBuilder;


public class SubTypesScanner extends AbstractScanner {


  public SubTypesScanner() {
    this(true); //exclude direct Object subtypes by default
  }


  public SubTypesScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings({"unchecked"})
  public void scan(final Object cls) {
    String className = getMetadataAdapter().getClassName(cls);
    String superclass = getMetadataAdapter().getSuperclassName(cls);

    if (acceptResult(superclass)) {
      getStore().put(superclass , className);
    }

    for (String anInterface : (List<String>) getMetadataAdapter().getInterfacesNames(cls)) {
      if (acceptResult(anInterface)) {
        getStore().put(anInterface , className);
      }
    }
  }
}
