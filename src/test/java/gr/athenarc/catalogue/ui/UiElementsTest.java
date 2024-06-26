package gr.athenarc.catalogue.ui;

import gr.athenarc.catalogue.ui.domain.*;
import gr.athenarc.catalogue.ui.service.FormsService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UiElementsTest {

    @Autowired
    FormsService formsService;

    final String fieldId = "f-test";
    final String groupId = "g-test";
    final String surveyId = "s-test";

    @Test
    @Order(1)
    void addField() {
        UiField field = createField();
        UiField res = formsService.addField(field);
        assert res.equals(field);
    }

    @Test
    @Order(2)
    void updateField() {
        UiField field = formsService.getField(fieldId);
        UiField res = formsService.updateField(fieldId, field);
        assert res.equals(field);
    }

    @Test
    @Order(3)
    void addGroup() {
        Group group = createGroup();
        Group res = formsService.addGroup(group);
        assert res.equals(group);
    }

    @Test
    @Order(4)
    void updateGroup() {
        Group group = formsService.getGroup(groupId);
        Group res = formsService.updateGroup(groupId, group);
        assert res.equals(group);
    }

    @Test
    @Order(5)
    void addSurvey() {
        Survey survey = createSurvey();
        Survey res = formsService.addSurvey(survey);
        assert res.equals(survey);
    }

    @Test
    @Order(6)
    void updateSurvey() {
        Survey survey = formsService.getSurvey(surveyId);
        Survey res = formsService.updateSurvey(surveyId, survey);
        assert res.equals(survey);
    }

    @Test
    @Order(7)
    void deleteSurvey() {
        formsService.deleteSurvey(surveyId);
    }

    @Test
    @Order(8)
    void deleteGroup() {
        formsService.deleteGroup(groupId);
    }

    @Test
    @Order(9)
    void deleteField() {
        formsService.deleteField(fieldId);
    }

    UiField createField() {
        UiField field = new UiField();
        field.setParentId(null);
        field.setId(fieldId);
        field.setName("Test Field");
        field.setParent(null);
        field.setIncludedInSnippet(true);

        field.setLabel(StyledString.of("Test Label"));

        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setType("composite");
        typeInfo.setMultiplicity(true);
        typeInfo.setVocabulary(null);
        List<String> values = new ArrayList<>();
        values.add("item 1");
        values.add("item 2");
        typeInfo.setValues(null);
        field.setTypeInfo(typeInfo);

        Form form = new Form();
        form.setAffects(new ArrayList<>());
        form.setMandatory(true);
        form.setDependsOn(null);
        form.setDescription(StyledString.of("Field Description"));
        form.setGroup(groupId);
        form.setImmutable(true);
        form.setDisplay(new Display(null, 1, true, false, null, null));
        form.setPlaceholder("Test placeholder");
        form.setSuggestion(StyledString.of("Test Suggestion"));
        field.setForm(form);

        Display display = new Display();
        display.setOrder(1);
        display.setPlacement("2");
        field.setDisplay(display);

        return field;
    }

    Group createGroup() {
        Group group = new Group();
        group.setId(groupId);
        group.setName("Test Group");
        group.setDescription("This is a group");
        group.setOrder(1);
        return group;
    }

    Survey createSurvey() {
        Survey survey = new Survey();
        survey.setId(surveyId);
        survey.setName("Test Survey");
        survey.setDescription("This is a survey");
        survey.setCreatedBy(null);
        survey.setCreationDate(new Date());
        survey.setModifiedBy(null);
        survey.setModificationDate(new Date());
        survey.setChapters(null);
        return survey;
    }

}
