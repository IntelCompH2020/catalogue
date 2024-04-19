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

package gr.athenarc.catalogue.controller;

import gr.athenarc.catalogue.service.ResourcePayloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payloads")
public class ResourcePayloadController {

    private final ResourcePayloadService resourcePayloadService;

    @Autowired
    public ResourcePayloadController(ResourcePayloadService resourcePayloadService) {
        this.resourcePayloadService = resourcePayloadService;
    }

    @GetMapping("{id}")
    public ResponseEntity<String> get(@RequestParam("resourceType") String resourceType, @PathVariable("id") String id) {
        String payload = resourcePayloadService.getRaw(resourceType, id);
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<String> createResource(@RequestParam("resourceType") String resourceType,
                                                @RequestBody String resource) {
        return new ResponseEntity<>(resourcePayloadService.addRaw(resourceType, resource), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public <T> ResponseEntity<?> update(@PathVariable("id") String id,
                                        @RequestParam("resourceType") String resourceType,
                                        @RequestBody T resource) throws NoSuchFieldException {
        return new ResponseEntity<>(resourcePayloadService.updateRaw(resourceType, id, (String) resource), HttpStatus.OK);
    }
}
