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

package gr.athenarc.catalogue.ui.config;

import eu.openminted.registry.core.service.ParserService;
import eu.openminted.registry.core.service.ResourceService;
import eu.openminted.registry.core.service.ResourceTypeService;
import eu.openminted.registry.core.service.SearchService;
import gr.athenarc.catalogue.service.GenericItemService;
import gr.athenarc.catalogue.service.id.IdGenerator;
import gr.athenarc.catalogue.ui.service.FormsService;
import gr.athenarc.catalogue.ui.service.JsonFileSavedFormsService;
import gr.athenarc.catalogue.ui.service.SimpleFormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UiFieldsSourceConfiguration {

    @Bean
    @ConditionalOnProperty(
            name = "ui.elements.json.enabled",
            havingValue = "true",
            matchIfMissing = false)
    FormsService jsonFileSavedUiFieldsService(@Value("${ui.elements.json.dir}") String jsonDir) {
        return new JsonFileSavedFormsService(jsonDir);
    }

    @Bean
    @Autowired
    @ConditionalOnProperty(
            name = "ui.elements.json.enabled",
            havingValue = "false",
            matchIfMissing = true)
    FormsService simpleUiFieldService(@Qualifier("catalogueGenericItemService") GenericItemService genericItemService,
                                      SearchService searchService, ResourceService resourceService,
                                      ResourceTypeService resourceTypeService, ParserService parserService,
                                      IdGenerator<String> idGenerator) {
        return new SimpleFormsService(genericItemService, idGenerator, searchService, resourceService, resourceTypeService, parserService);
    }
}
