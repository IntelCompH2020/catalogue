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

package gr.athenarc.catalogue.ui.controller;

import gr.athenarc.catalogue.ClasspathUtils;
import gr.athenarc.catalogue.ui.domain.*;
import gr.athenarc.catalogue.ui.service.FormsService;
import gr.athenarc.catalogue.ui.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("ui")
public class UiController {

    private final FormsService formsService;
    private final ModelService modelService;

    @Autowired
    public UiController(FormsService formsService, ModelService modelService) {
        this.formsService = formsService;
        this.modelService = modelService;

    }

    @GetMapping(value = "{className}/fields/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UiField> createFields(@PathVariable("className") String name) throws ClassNotFoundException {
        return formsService.createFields(name, null);
    }

    @GetMapping(value = "{className}/fields", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UiField> getFields(@PathVariable("className") String name) {
        return formsService.getFields();
    }

    @GetMapping(value = "form/model/{id}/flat", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GroupedFields<UiField>> getModelFlat(@PathVariable("id") String id) {
        return formsService.getSurveyModelFlat(id);
    }

    @GetMapping(value = "form/model/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Model getSurveyModel(@PathVariable("id") String id) {
        return modelService.get(id);
//        return formsService.getSurveyModel(surveyId);
    }


    @GetMapping(value = "vocabularies/map", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getVocabularies() {
        return getEnumsMap("gr.athenarc.xsd2java");
    }

    private Map<String, List<String>> getEnumsMap(String packageName) {
        Map<String, List<String>> enumsMap = new HashMap<>();
        Set<Class<?>> allClasses = ClasspathUtils.findAllEnums(packageName);
        for (Class<?> c : allClasses) {
            if (c.isEnum()) {
                enumsMap.put(c.getSimpleName(), new ArrayList<>(Arrays.stream(c.getEnumConstants()).map(Object::toString).collect(Collectors.toList())));
            }
        }
        return enumsMap;
    }
}
