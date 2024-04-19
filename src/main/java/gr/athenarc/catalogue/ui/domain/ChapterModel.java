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

public class ChapterModel {

    Chapter chapter;
    List<GroupedFields<FieldGroup>> groupedFieldsList;

    public ChapterModel() {}

    public ChapterModel(Chapter chapter, List<GroupedFields<FieldGroup>> groupedFieldsList) {
        this.chapter = chapter;
        this.groupedFieldsList = groupedFieldsList;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<GroupedFields<FieldGroup>> getGroupedFieldsList() {
        return groupedFieldsList;
    }

    public void setGroupedFieldsList(List<GroupedFields<FieldGroup>> groupedFieldsList) {
        this.groupedFieldsList = groupedFieldsList;
    }
}
