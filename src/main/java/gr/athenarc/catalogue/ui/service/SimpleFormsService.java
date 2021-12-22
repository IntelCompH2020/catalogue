package gr.athenarc.catalogue.ui.service;

import eu.openminted.registry.core.domain.Browsing;
import eu.openminted.registry.core.domain.FacetFilter;
import eu.openminted.registry.core.domain.Resource;
import eu.openminted.registry.core.domain.ResourceType;
import eu.openminted.registry.core.service.ParserService;
import eu.openminted.registry.core.service.ResourceService;
import eu.openminted.registry.core.service.ResourceTypeService;
import eu.openminted.registry.core.service.SearchService;
import gr.athenarc.catalogue.LoggingUtils;
import gr.athenarc.catalogue.ReflectUtils;
import gr.athenarc.catalogue.exception.ResourceException;
import gr.athenarc.catalogue.exception.ResourceNotFoundException;
import gr.athenarc.catalogue.service.GenericItemService;
import gr.athenarc.catalogue.service.id.IdCreator;
import gr.athenarc.catalogue.ui.domain.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleFormsService implements FormsService {

    private static final Logger logger = LogManager.getLogger(SimpleFormsService.class);
    private static final String FIELD_RESOURCE_TYPE_NAME = "field";
    private static final String GROUP_RESOURCE_TYPE_NAME = "group";
    private static final String SURVEY_RESOURCE_TYPE_NAME = "survey";
    private final GenericItemService genericItemService;
    private final IdCreator<String> idCreator;
    public final SearchService searchService;
    public final ResourceService resourceService;
    public final ResourceTypeService resourceTypeService;
    public final ParserService parserPool;

    public SimpleFormsService(GenericItemService genericItemService,
                              IdCreator<String> idCreator,
                              SearchService searchService,
                              ResourceService resourceService,
                              ResourceTypeService resourceTypeService,
                              ParserService parserPool) {
        this.genericItemService = genericItemService;
        this.idCreator = idCreator;
        this.searchService = searchService;
        this.resourceService = resourceService;
        this.resourceTypeService = resourceTypeService;
        this.parserPool = parserPool;
    }

    @Override
    public UiField addField(UiField field) {
        field = add(field, FIELD_RESOURCE_TYPE_NAME);
        return field;
    }

    @Override
    public UiField updateField(String id, UiField field) throws ResourceNotFoundException {
        field = update(id, field, FIELD_RESOURCE_TYPE_NAME);
        return field;
    }

    @Override
    public void deleteField(String fieldId) throws ResourceNotFoundException {
        delete(fieldId, FIELD_RESOURCE_TYPE_NAME);
    }

    @Override
    public UiField getField(String id) {
        UiField field = genericItemService.get(FIELD_RESOURCE_TYPE_NAME, id);
        setFormDependsOnName(field);
        return field;
    }

    @Override
    public Browsing<UiField> browseFields(FacetFilter filter) {
        Browsing<UiField> results = genericItemService.getResults(filter);
        for (UiField field : results.getResults()) {
            setFormDependsOnName(field);
        }
        return results;
    }

    @Override
    public List<UiField> getFields() {
        FacetFilter ff = new FacetFilter();
        ff.setQuantity(10000);
        ff.setResourceType(FIELD_RESOURCE_TYPE_NAME);
        return browseFields(ff).getResults();
    }

    @Override
    public Group addGroup(Group group) {
        group = add(group, GROUP_RESOURCE_TYPE_NAME);
        return group;
    }

    @Override
    public Group updateGroup(String id, Group group) {
        group = update(id, group, GROUP_RESOURCE_TYPE_NAME);
        return group;
    }

    @Override
    public void deleteGroup(String groupId) throws ResourceNotFoundException {
        delete(groupId, GROUP_RESOURCE_TYPE_NAME);
    }

    @Override
    public Group getGroup(String id) {
        return genericItemService.get(GROUP_RESOURCE_TYPE_NAME, id);
    }

    @Override
    public List<Group> getGroups() {
        FacetFilter ff = new FacetFilter();
        ff.setQuantity(10000);
        ff.setResourceType(GROUP_RESOURCE_TYPE_NAME);
        return (List) genericItemService.getResults(ff).getResults();
    }

    @Override
    public Survey addSurvey(Survey survey) {
        createChapterIds(survey);
        survey = add(survey, SURVEY_RESOURCE_TYPE_NAME);
        return survey;
    }

    @Override
    public Survey updateSurvey(String id, Survey survey) {
        createChapterIds(survey);
        survey = update(id, survey, SURVEY_RESOURCE_TYPE_NAME);
        return survey;
    }

    @Override
    public void deleteSurvey(String surveyId) throws ResourceNotFoundException {
        delete(surveyId, SURVEY_RESOURCE_TYPE_NAME);
    }

    @Override
    public Survey getSurvey(String id) {
        return genericItemService.get(SURVEY_RESOURCE_TYPE_NAME, id);
    }

    @Override
    public List<Survey> getSurveys() {
        FacetFilter ff = new FacetFilter();
        ff.setQuantity(10000);
        ff.setResourceType(SURVEY_RESOURCE_TYPE_NAME);
        return (List) genericItemService.getResults(ff).getResults();
    }

    // TODO: REWRITE THIS METHOD
    @Override
    public List<FieldGroup> createFieldGroups(List<UiField> fields) {
        Map<String, FieldGroup> topLevelFieldGroupMap;
        Set<String> fieldIds = fields.stream().map(UiField::getId).collect(Collectors.toSet());
        topLevelFieldGroupMap = fields
                .stream()
                .filter(Objects::nonNull)
                .filter(field -> field.getParentId() == null)
                .filter(field -> "composite".equals(field.getTypeInfo().getType()))
                .map(FieldGroup::new)
                .collect(Collectors.toMap(f -> (f.getField().getId()), Function.identity()));

        List<UiField> leftOvers = sortFieldsByParentId(fields);

        Map<String, FieldGroup> tempFieldGroups = new HashMap<>();
        tempFieldGroups.putAll(topLevelFieldGroupMap);
        int retries = 0;
        do {
            retries++;
            fields = leftOvers;
            leftOvers = new ArrayList<>();
            for (UiField field : fields) {
                FieldGroup fieldGroup = new FieldGroup(field, new ArrayList<>());

                // Fix problem when Parent ID is defined but Field with that ID is not contained to this group of fields.
                if (!fieldIds.contains(field.getParentId())) {
                    field = new UiField(field);
                    field.setParentId(null);
                }

                if (field.getParentId() == null) {
                    topLevelFieldGroupMap.putIfAbsent(field.getId(), fieldGroup);
                } else if (topLevelFieldGroupMap.containsKey(field.getParentId())) {
                    topLevelFieldGroupMap.get(field.getParentId()).getSubFieldGroups().add(fieldGroup);
                    tempFieldGroups.putIfAbsent(field.getId(), fieldGroup);
                } else if (tempFieldGroups.containsKey(field.getParentId())) {
                    tempFieldGroups.get(field.getParentId()).getSubFieldGroups().add(fieldGroup);
                    tempFieldGroups.putIfAbsent(field.getId(), fieldGroup);
                } else {
                    leftOvers.add(field);
                }
            }

        } while (!leftOvers.isEmpty() && retries < 10);
        return new ArrayList<>(topLevelFieldGroupMap.values());
    }

    @Override
    public Map<String, List<GroupedFields<FieldGroup>>> getSurveyModel(String surveyId) { // TODO: refactor return type (+add chapter info)
        Map<String, List<GroupedFields<FieldGroup>>> chapterGroupedFieldGroups = new HashMap<>();
        Survey survey = getSurvey(surveyId);
        List<GroupedFields<UiField>> groupedFieldsList = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for (Chapter chapter : survey.getChapters()) {
            chapterGroupedFieldGroups.put(chapter.getName(), getChapterModel(surveyId, chapter.getId()));
        }
        return chapterGroupedFieldGroups;
    }

    public List<GroupedFields<FieldGroup>> getChapterModel(String surveyId, String chapterId) {
        List<GroupedFields<FieldGroup>> groupedFieldGroups = new ArrayList<>();
        List<GroupedFields<UiField>> groupedFieldsList = getChapterModelFlat(surveyId, chapterId);

        for (GroupedFields<UiField> groupedFields : groupedFieldsList) {
            GroupedFields<FieldGroup> groupedFieldGroup = new GroupedFields<>();

            groupedFieldGroup.setGroup(groupedFields.getGroup());
            List<FieldGroup> fieldGroups = createFieldGroups(groupedFields.getFields());
            groupedFieldGroup.setFields(fieldGroups);

            int total = 0;
            for (UiField f : groupedFields.getFields()) {
                if (f.getForm().getMandatory() != null && f.getForm().getMandatory()
                        && f.getTypeInfo().getType() != null && !f.getTypeInfo().getType().equals("composite")) {
                    total += 1;
                }
            }

            int topLevel = 0;
            for (FieldGroup fg : fieldGroups) {
                if (fg.getField().getForm().getMandatory() != null && fg.getField().getForm().getMandatory()) {
                    topLevel += 1;
                }
            }
            RequiredFields requiredFields = new RequiredFields(topLevel, total);
            groupedFieldGroup.setRequired(requiredFields);

            groupedFieldGroups.add(groupedFieldGroup);
        }

        return groupedFieldGroups;
    }

    public List<GroupedFields<UiField>> getChapterModelFlat(String surveyId, String chapterId) {
        Survey survey = getSurvey(surveyId);
        List<GroupedFields<UiField>> groupedFieldsList = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for (Chapter chapter : survey.getChapters()) {
            if (chapter.getId().equals(chapterId)) {
                groups.addAll(chapter.getSections().stream().map(this::getGroup).collect(Collectors.toList()));
            }
        }

        for (Group group : groups) {
            GroupedFields<UiField> groupedFields = new GroupedFields<>();

            groupedFields.setGroup(group);
            groupedFields.setFields(getFieldsByGroup(group.getId()));

            groupedFieldsList.add(groupedFields);
        }

        return groupedFieldsList;
    }

    @Override
    public List<GroupedFields<UiField>> getSurveyModelFlat(String surveyId) {
        Survey survey = getSurvey(surveyId);
        List<GroupedFields<UiField>> groupedFieldsList = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for (Chapter chapter : survey.getChapters()) {
            groups.addAll(chapter.getSections().stream().map(this::getGroup).collect(Collectors.toList()));
        }

        for (Group group : groups) {
            GroupedFields<UiField> groupedFields = new GroupedFields<>();

            groupedFields.setGroup(group);
            groupedFields.setFields(getFieldsByGroup(group.getId()));

            groupedFieldsList.add(groupedFields);
        }

        return groupedFieldsList;
    }

    private List<UiField> sortFieldsByParentId(List<UiField> fields) {
        List<UiField> sorted = fields.stream().filter(f -> f.getParentId() != null).sorted(Comparator.comparing(UiField::getParentId)).collect(Collectors.toList());
        sorted.addAll(fields.stream().filter(f -> f.getParentId() == null).collect(Collectors.toList()));
        return sorted;
    }

    public <T> T add(T obj, String resourceTypeName) {
        ResourceType resourceType = resourceTypeService.getResourceType(resourceTypeName);
        String id = null;
        try {
            id = ReflectUtils.getId(obj.getClass(), obj);
            if (id == null) {
                id = idCreator.createId(resourceTypeName.charAt(0) + "-");
                ReflectUtils.setId(obj.getClass(), obj, id);
            }

        } catch (NoSuchFieldException e) {
            logger.error(e);
        }

        Resource resource = new Resource();
        resource.setResourceTypeName(resourceTypeName);
        resource.setResourceType(resourceType);
        resource.setPayload(parserPool.serialize(obj, ParserService.ParserServiceTypes.fromString(resourceType.getPayloadType())));
        logger.trace(LoggingUtils.addResource(resourceTypeName, id, obj));
        resourceService.addResource(resource);
        return obj;
    }

    public <T> T update(String id, T obj, String resourceTypeName) {
        Resource existing = null;
        try {
            if (!id.equals(ReflectUtils.getId(obj.getClass(), obj))) {
                throw new ResourceException("You are not allowed to modify the id of a resource.", HttpStatus.CONFLICT);
            }
            existing = genericItemService.searchResource(resourceTypeName, id, true);
            existing.setPayload(parserPool.serialize(obj, ParserService.ParserServiceTypes.JSON));
        } catch (NoSuchFieldException e) {
            logger.error(e);
        }

        logger.trace(LoggingUtils.updateResource(resourceTypeName, id, obj));
        resourceService.updateResource(existing);
        return obj;
    }

    public <T> void delete(String id, String resourceTypeName) throws ResourceNotFoundException {
        Resource resource = null;
        Class<?> clazz = genericItemService.getClassFromResourceType(resourceTypeName);
        resource = genericItemService.searchResource(resourceTypeName, id, true);
        T obj = (T) parserPool.deserialize(resource, clazz);
        logger.trace(LoggingUtils.deleteResource(resourceTypeName, id, obj));
        resourceService.deleteResource(resource.getId());
//        return obj;
    }

    private void createChapterIds(Survey survey) {
        for (Chapter chapter : survey.getChapters()) {
            if (chapter.getId() == null || "".equals(chapter.getId())) {
                chapter.setId(idCreator.createId( "c-"));
            }
        }
    }

}