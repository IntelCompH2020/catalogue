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

package gr.athenarc.catalogue.ui.domain;

import java.util.ArrayList;
import java.util.List;

public class FieldGroup {

    private UiField field;
    private List<FieldGroup> subFieldGroups = new ArrayList<>();

    public FieldGroup() {}

    public FieldGroup(UiField field) {
        this.field = field;
    }

    public FieldGroup(UiField field, List<FieldGroup> subFieldGroups) {
        this.field = field;
        this.subFieldGroups = subFieldGroups;
    }

    public UiField getField() {
        return field;
    }

    public void setField(UiField field) {
        this.field = field;
    }

    public List<FieldGroup> getSubFieldGroups() {
        return subFieldGroups;
    }

    public void setSubFieldGroups(List<FieldGroup> subFieldGroups) {
        this.subFieldGroups = subFieldGroups;
    }
}
