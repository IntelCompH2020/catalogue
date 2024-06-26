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

import eu.openminted.registry.core.domain.FacetFilter;
import eu.openminted.registry.core.domain.Paging;
import gr.athenarc.catalogue.service.GenericItemService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "items")
public class GenericItemController {

    private final GenericItemService genericResourceService;

    @Autowired
    GenericItemController(@Qualifier("catalogueGenericItemService") GenericItemService genericResourceService) {
        this.genericResourceService = genericResourceService;
    }

    @PostMapping()
    public <T> ResponseEntity<?> create(@RequestParam("resourceType") String resourceType,
                                        @RequestBody T resource) {
        T createdResource;
        createdResource = genericResourceService.add(resourceType, resource);
        return new ResponseEntity<>(createdResource, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public <T> ResponseEntity<?> update(@PathVariable("id") String id,
                                        @RequestParam("resourceType") String resourceType,
                                        @RequestParam(value = "raw", defaultValue = "false") boolean raw,
                                        @RequestBody T resource) throws NoSuchFieldException {
        T createdResource;
        createdResource = genericResourceService.update(resourceType, id, resource);
        return new ResponseEntity<>(createdResource, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public <T> ResponseEntity<?> delete(@PathVariable("id") String id, @RequestParam("resourceType") String resourceType) {
        return new ResponseEntity<>(genericResourceService.delete(resourceType, id), HttpStatus.OK);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "Keyword to refine the search", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "from", value = "Starting index in the result set", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "quantity", value = "Quantity to be fetched", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "resourceType", value = "Resource type name", dataTypeClass = String.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "order", value = "asc / desc", dataTypeClass = String.class, paramType = "query"),
            @ApiImplicitParam(name = "orderField", value = "Order field", dataTypeClass = String.class, paramType = "query")
    })
    @GetMapping()
    public ResponseEntity<Paging<?>> browseByResourceType(@ApiIgnore @RequestParam Map<String, Object> allRequestParams) {
        FacetFilter ff = createFacetFilter(allRequestParams);
        return new ResponseEntity<>(genericResourceService.getResults(ff), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public <T> ResponseEntity<T> get(@RequestParam("resourceType") String resourceType, @PathVariable("id") String id) {
        T ret = genericResourceService.get(resourceType, id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<?> getByField(@RequestParam("resourceType") String resourceType, @RequestParam(value = "field") String field, @RequestParam(value = "value") String value) {
        Object ret = genericResourceService.get(resourceType, field, value, true);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    public static FacetFilter createFacetFilter(Map<String, Object> allRequestParams) {
        FacetFilter ff = new FacetFilter();
        ff.setKeyword(allRequestParams.get("query") != null ? (String) allRequestParams.remove("query") : "");
        ff.setFrom(allRequestParams.get("from") != null ? Integer.parseInt((String) allRequestParams.remove("from")) : 0);
        ff.setQuantity(allRequestParams.get("quantity") != null ? Integer.parseInt((String) allRequestParams.remove("quantity")) : 10);
        ff.setResourceType((String) allRequestParams.remove("resourceType"));
        ff.setFilter(allRequestParams);
        Map<String, Object> sort = new HashMap<>();
        Map<String, Object> order = new HashMap<>();
        String orderDirection = allRequestParams.get("order") != null ? (String) allRequestParams.remove("order") : "asc";
        String orderField = allRequestParams.get("orderField") != null ? (String) allRequestParams.remove("orderField") : null;
        if (orderField != null) {
            order.put("order", orderDirection);
            sort.put(orderField, order);
            ff.setOrderBy(sort);
        }
        return ff;
    }
}
