/*
 * Copyright 2021-2024 OpenAIRE AMKE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.catalogue.config;

import gr.athenarc.catalogue.ClasspathUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import static gr.athenarc.catalogue.ClasspathUtils.getClassesWithoutInterfaces;

@Configuration
public class CatalogueConfiguration {

    private static final Logger logger = LogManager.getLogger(CatalogueConfiguration.class);

    @Bean
    JAXBContext catalogueJAXBContext() throws JAXBException {
        Class<?>[] classes = ClasspathUtils.classesToArray(getClassesWithoutInterfaces("gr.athenarc.xsd2java"));
        return JAXBContext.newInstance(classes);
    }
}
