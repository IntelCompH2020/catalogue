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

import java.util.List;

public class GroupedFields <T> {

    private Group group;
    private List<T> fields;
    private RequiredFields required;

    public GroupedFields() {
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<T> getFields() {
        return fields;
    }

    public void setFields(List<T> fields) {
        this.fields = fields;
    }

    public RequiredFields getRequired() {
        return required;
    }

    public void setRequired(RequiredFields required) {
        this.required = required;
    }
}
