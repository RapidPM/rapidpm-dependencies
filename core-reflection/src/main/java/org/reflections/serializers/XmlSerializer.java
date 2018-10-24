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
package org.reflections.serializers;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.Store;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.Utils;

import java.io.*;
import java.lang.reflect.Constructor;


public class XmlSerializer implements Serializer {

  public Reflections read(InputStream inputStream) {
    Reflections reflections;
    try {
      Constructor<Reflections> constructor = Reflections.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      reflections = constructor.newInstance();
    } catch (Exception e) {
      reflections = new Reflections(new ConfigurationBuilder());
    }

    try {
      Document document = new SAXReader().read(inputStream);
      for (Object e1 : document.getRootElement().elements()) {
        Element index = (Element) e1;
        for (Object e2 : index.elements()) {
          Element entry = (Element) e2;
          Element key = entry.element("key");
          Element values = entry.element("values");
          for (Object o3 : values.elements()) {
            Element value = (Element) o3;
            reflections.getStore().getOrCreate(index.getName()).put(key.getText() , value.getText());
          }
        }
      }
    } catch (DocumentException e) {
      throw new ReflectionsException("could not read." , e);
    } catch (Throwable e) {
      throw new RuntimeException("Could not read. Make sure relevant dependencies exist on classpath." , e);
    }

    return reflections;
  }

  public File save(final Reflections reflections , final String filename) {
    File file = Utils.prepareFile(filename);


    try {
      Document document = createDocument(reflections);
      XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file) , OutputFormat.createPrettyPrint());
      xmlWriter.write(document);
      xmlWriter.close();
    } catch (IOException e) {
      throw new ReflectionsException("could not save to file " + filename , e);
    } catch (Throwable e) {
      throw new RuntimeException("Could not save to file " + filename + ". Make sure relevant dependencies exist on classpath." , e);
    }

    return file;
  }

  public String toString(final Reflections reflections) {
    Document document = createDocument(reflections);

    try {
      StringWriter writer = new StringWriter();
      XMLWriter xmlWriter = new XMLWriter(writer , OutputFormat.createPrettyPrint());
      xmlWriter.write(document);
      xmlWriter.close();
      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  private Document createDocument(final Reflections reflections) {
    Store map = reflections.getStore();

    Document document = DocumentFactory.getInstance().createDocument();
    Element root = document.addElement("Reflections");
    for (String indexName : map.keySet()) {
      Element indexElement = root.addElement(indexName);
      for (String key : map.get(indexName).keySet()) {
        Element entryElement = indexElement.addElement("entry");
        entryElement.addElement("key").setText(key);
        Element valuesElement = entryElement.addElement("values");
        for (String value : map.get(indexName).get(key)) {
          valuesElement.addElement("value").setText(value);
        }
      }
    }
    return document;
  }
}
