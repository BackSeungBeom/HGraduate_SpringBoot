package hgraduate.seed;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;

import hgraduate.course.entity.Course;
import hgraduate.course.entity.CourseAttribute;
import hgraduate.course.entity.CourseTag;
import hgraduate.course.entity.CourseTagId;
import hgraduate.course.entity.Prerequisite;
import hgraduate.course.entity.PrerequisiteRelationType;
import hgraduate.course.entity.Tag;
import hgraduate.course.repository.CourseAttributeRepository;
import hgraduate.course.repository.CourseRepository;
import hgraduate.course.repository.CourseTagRepository;
import hgraduate.course.repository.PrerequisiteRepository;
import hgraduate.course.repository.TagRepository;
import hgraduate.curriculum.entity.CurriculumStatus;
import hgraduate.curriculum.entity.CurriculumVersion;
import hgraduate.curriculum.repository.CurriculumVersionRepository;
import hgraduate.requirement.entity.GraduationRequirement;
import hgraduate.requirement.entity.Requirement;
import hgraduate.requirement.entity.RequirementCategory;
import hgraduate.requirement.entity.RequirementUnit;
import hgraduate.requirement.entity.RequirementValueType;
import hgraduate.requirement.repository.GraduationRequirementRepository;
import hgraduate.requirement.repository.RequirementCategoryRepository;
import hgraduate.requirement.repository.RequirementRepository;
import hgraduate.rule.entity.Action;
import hgraduate.rule.entity.DuplicatePolicy;
import hgraduate.rule.entity.DuplicationRule;
import hgraduate.rule.entity.ExpressionType;
import hgraduate.rule.entity.GraduationRule;
import hgraduate.rule.entity.RuleExpressionNode;
import hgraduate.rule.repository.ActionRepository;
import hgraduate.rule.repository.DuplicationRuleRepository;
import hgraduate.rule.repository.GraduationRuleRepository;
import hgraduate.rule.repository.RuleExpressionNodeRepository;
import hgraduate.rulemapping.entity.CourseRequirementMapping;
import hgraduate.rulemapping.entity.MappingDuplicationPolicy;
import hgraduate.rulemapping.repository.CourseRequirementMappingRepository;
import hgraduate.student.entity.Department;
import hgraduate.student.repository.DepartmentRepository;

// Seed Data Import (CLAUDE.md 로드맵) — seed-data/*_v1.1.xlsx 15개 파일을 FK 의존 순서대로 적재한다.
// rule_id는 course_requirement_mapping.xlsx 원본 문자열 그대로 저장하고(CourseRequirementMapping.ruleId는 String),
// courses의 _legacy_ 컬럼도 변환 없이 원본 값 그대로 저장한다 (schema-migration-spec.md / CLAUDE.md 결정사항).
@Component
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    // seed-data/ 위치는 문서에 규정이 없어 새로 정한 값 — gradlew bootRun 기본 작업 디렉터리(HGraduate/HGraduate) 기준 상대경로.
    @Value("${hgraduate.seed.data-dir:../seed-data}")
    private String dataDir;

    private final DepartmentRepository departmentRepository;
    private final CurriculumVersionRepository curriculumVersionRepository;
    private final RequirementCategoryRepository requirementCategoryRepository;
    private final RequirementRepository requirementRepository;
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;
    private final CourseAttributeRepository courseAttributeRepository;
    private final GraduationRuleRepository graduationRuleRepository;
    private final RuleExpressionNodeRepository ruleExpressionNodeRepository;
    private final ActionRepository actionRepository;
    private final GraduationRequirementRepository graduationRequirementRepository;
    private final CourseRequirementMappingRepository courseRequirementMappingRepository;
    private final DuplicationRuleRepository duplicationRuleRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final CourseTagRepository courseTagRepository;
    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    public SeedDataRunner(
            DepartmentRepository departmentRepository,
            CurriculumVersionRepository curriculumVersionRepository,
            RequirementCategoryRepository requirementCategoryRepository,
            RequirementRepository requirementRepository,
            TagRepository tagRepository,
            CourseRepository courseRepository,
            CourseAttributeRepository courseAttributeRepository,
            GraduationRuleRepository graduationRuleRepository,
            RuleExpressionNodeRepository ruleExpressionNodeRepository,
            ActionRepository actionRepository,
            GraduationRequirementRepository graduationRequirementRepository,
            CourseRequirementMappingRepository courseRequirementMappingRepository,
            DuplicationRuleRepository duplicationRuleRepository,
            PrerequisiteRepository prerequisiteRepository,
            CourseTagRepository courseTagRepository,
            EntityManager entityManager,
            PlatformTransactionManager transactionManager) {
        this.departmentRepository = departmentRepository;
        this.curriculumVersionRepository = curriculumVersionRepository;
        this.requirementCategoryRepository = requirementCategoryRepository;
        this.requirementRepository = requirementRepository;
        this.tagRepository = tagRepository;
        this.courseRepository = courseRepository;
        this.courseAttributeRepository = courseAttributeRepository;
        this.graduationRuleRepository = graduationRuleRepository;
        this.ruleExpressionNodeRepository = ruleExpressionNodeRepository;
        this.actionRepository = actionRepository;
        this.graduationRequirementRepository = graduationRequirementRepository;
        this.courseRequirementMappingRepository = courseRequirementMappingRepository;
        this.duplicationRuleRepository = duplicationRuleRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.courseTagRepository = courseTagRepository;
        this.entityManager = entityManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // 모든 PK는 Excel 원본 ID를 그대로 쓰고 @GeneratedValue가 없다 — repository.save()의 기본 isNew() 판정은
    // "ID != null"이면 신규가 아니라고 보고 merge()를 호출하는데, 이러면 @MapsId 엔티티(CourseAttribute, CourseTag)에서
    // 식별자 조립이 깨진다(org.hibernate.AssertionFailure: null identifier). 시드 데이터는 항상 신규 삽입이므로
    // EntityManager.persist()를 직접 사용한다.
    private <T> void persistAll(List<T> entities) {
        transactionTemplate.executeWithoutResult(status -> {
            for (T entity : entities) {
                entityManager.persist(entity);
            }
            entityManager.flush();
            entityManager.clear();
        });
    }

    @Override
    public void run(String... args) throws Exception {
        loadDepartments();
        loadCurriculumVersions();
        loadCategoryTree();
        loadRequirements();
        loadTags();
        loadCourses();
        loadCourseAttributes();
        loadGraduationRules();
        loadRuleExpressions();
        loadRuleActions();
        loadGraduationRequirements();
        loadCourseRequirementMappings();
        loadDuplicateRules();
        loadPrerequisites();
        loadCourseTags();
    }

    private Sheet openSheet(String fileName, Workbook[] out) throws IOException {
        File file = new File(dataDir, fileName);
        try (InputStream is = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(is);
            out[0] = workbook;
            return workbook.getSheetAt(0);
        }
    }

    private void validateCount(String table, long expected, long actual) {
        if (expected == actual) {
            log.info("[VALIDATE] {}: xlsx={}, db={} -> OK", table, expected, actual);
        } else {
            log.warn("[VALIDATE] {}: xlsx={}, db={} -> MISMATCH", table, expected, actual);
        }
    }

    private void loadDepartments() throws IOException {
        String table = "departments";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("departments_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (departmentRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, departmentRepository.count());
            } else {
                List<Department> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Department.builder()
                            .departmentId(ExcelUtil.getLong(row, 0))
                            .departmentCode(ExcelUtil.getString(row, 1))
                            .departmentName(ExcelUtil.getString(row, 2))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), departmentRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCurriculumVersions() throws IOException {
        String table = "curriculum_versions";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("curriculum_versions_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (curriculumVersionRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, curriculumVersionRepository.count());
            } else {
                List<CurriculumVersion> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(CurriculumVersion.builder()
                            .curriculumVersionId(ExcelUtil.getLong(row, 0))
                            .version(ExcelUtil.getString(row, 1))
                            .creditSystem(ExcelUtil.getInteger(row, 2))
                            .effectiveFrom(ExcelUtil.getInteger(row, 3))
                            .effectiveTo(ExcelUtil.getInteger(row, 4))
                            .status(CurriculumStatus.valueOf(ExcelUtil.getString(row, 5)))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), curriculumVersionRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCategoryTree() throws IOException {
        String table = "category_tree";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("category_tree_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (requirementCategoryRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, requirementCategoryRepository.count());
            } else {
                List<RequirementCategory> entities = new ArrayList<>();
                for (Row row : rows) {
                    Long parentId = ExcelUtil.getLong(row, 1);
                    entities.add(RequirementCategory.builder()
                            .categoryId(ExcelUtil.getLong(row, 0))
                            .parentCategory(parentId == null ? null : requirementCategoryRepository.getReferenceById(parentId))
                            .categoryName(ExcelUtil.getString(row, 2))
                            .displayOrder(ExcelUtil.getInteger(row, 3))
                            .build());
                }
                // 자기참조 FK(parent_category_id) — 부모 행이 자식보다 먼저 insert되도록 PK 오름차순 정렬 후 저장.
                // 이 정렬은 parent_id < 자기_id라는 현재 데이터의 불변식에 의존한다. 이 조건이 깨지는 데이터가
                // 추가되면 FK 위반으로 실패하니, 그 경우 위상정렬(topological sort)로 교체가 필요하다.
                entities.sort(Comparator.comparing(RequirementCategory::getCategoryId));
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), requirementCategoryRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadRequirements() throws IOException {
        String table = "requirements";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("requirements_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (requirementRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, requirementRepository.count());
            } else {
                List<Requirement> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Requirement.builder()
                            .requirementId(ExcelUtil.getLong(row, 0))
                            .requirementCode(ExcelUtil.getString(row, 1))
                            .category(requirementCategoryRepository.getReferenceById(ExcelUtil.getLong(row, 2)))
                            .name(ExcelUtil.getString(row, 3))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), requirementRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadTags() throws IOException {
        String table = "tags";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("tags_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (tagRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, tagRepository.count());
            } else {
                List<Tag> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Tag.builder()
                            .tagId(ExcelUtil.getLong(row, 0))
                            .tagName(ExcelUtil.getString(row, 1))
                            .description(ExcelUtil.getString(row, 2))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), tagRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCourses() throws IOException {
        String table = "courses";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("courses_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (courseRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, courseRepository.count());
            } else {
                List<Course> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Course.builder()
                            .courseId(ExcelUtil.getLong(row, 0))
                            .courseCode(ExcelUtil.getString(row, 1))
                            .courseNameKo(ExcelUtil.getString(row, 2))
                            .courseNameEn(ExcelUtil.getString(row, 3))
                            .department(departmentRepository.getReferenceById(ExcelUtil.getLong(row, 4)))
                            .curriculumVersion(curriculumVersionRepository.getReferenceById(ExcelUtil.getLong(row, 5)))
                            .legacyCategory(ExcelUtil.getString(row, 6))
                            .legacySubcategory(ExcelUtil.getString(row, 7))
                            .courseType(ExcelUtil.getString(row, 8))
                            .legacyIsMajor(ExcelUtil.getBoolean(row, 9))
                            .legacyIsGeneral(ExcelUtil.getBoolean(row, 10))
                            .legacyIsRequired(ExcelUtil.getBoolean(row, 11))
                            .legacyIsElective(ExcelUtil.getBoolean(row, 12))
                            .recommendedGrade(ExcelUtil.getInteger(row, 13))
                            .recommendedSemester(ExcelUtil.getInteger(row, 14))
                            .open(ExcelUtil.getBoolean(row, 15))
                            .active(ExcelUtil.getBoolean(row, 16))
                            .createdAt(ExcelUtil.getLocalDateTime(row, 17))
                            .updatedAt(ExcelUtil.getLocalDateTime(row, 18))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), courseRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCourseAttributes() throws IOException {
        String table = "course_attributes";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("course_attributes_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (courseAttributeRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, courseAttributeRepository.count());
            } else {
                List<CourseAttribute> entities = new ArrayList<>();
                for (Row row : rows) {
                    Long courseId = ExcelUtil.getLong(row, 0);
                    entities.add(CourseAttribute.builder()
                            .courseId(courseId)
                            .course(courseRepository.getReferenceById(courseId))
                            .credit(ExcelUtil.getBigDecimal(row, 1))
                            .lectureCredit(ExcelUtil.getBigDecimal(row, 2))
                            .labCredit(ExcelUtil.getBigDecimal(row, 3))
                            .designCredit(ExcelUtil.getBigDecimal(row, 4))
                            .englishRatio(ExcelUtil.getBigDecimal(row, 5))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), courseAttributeRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadGraduationRules() throws IOException {
        String table = "graduation_rules";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("graduation_rules_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (graduationRuleRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, graduationRuleRepository.count());
            } else {
                List<GraduationRule> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(GraduationRule.builder()
                            .ruleId(ExcelUtil.getLong(row, 0))
                            .ruleType(ExcelUtil.getString(row, 1))
                            .function(ExcelUtil.getString(row, 2))
                            .target(ExcelUtil.getString(row, 3))
                            .operator(ExcelUtil.getString(row, 4))
                            .value(ExcelUtil.getString(row, 5))
                            .action(ExcelUtil.getString(row, 6))
                            .priority(ExcelUtil.getInteger(row, 7))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), graduationRuleRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadRuleExpressions() throws IOException {
        String table = "rule_expression";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("rule_expression_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (ruleExpressionNodeRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, ruleExpressionNodeRepository.count());
            } else {
                List<RuleExpressionNode> entities = new ArrayList<>();
                for (Row row : rows) {
                    Long parentId = ExcelUtil.getLong(row, 6);
                    entities.add(RuleExpressionNode.builder()
                            .expressionId(ExcelUtil.getLong(row, 0))
                            .rule(graduationRuleRepository.getReferenceById(ExcelUtil.getLong(row, 1)))
                            .expressionType(ExpressionType.valueOf(ExcelUtil.getString(row, 2)))
                            .leftOperand(ExcelUtil.getString(row, 3))
                            .operator(ExcelUtil.getString(row, 4))
                            .rightOperand(ExcelUtil.getString(row, 5))
                            .parentExpression(parentId == null ? null : ruleExpressionNodeRepository.getReferenceById(parentId))
                            .sortOrder(ExcelUtil.getInteger(row, 7))
                            .build());
                }
                // 자기참조 FK(parent_expression) — 부모 행이 자식보다 먼저 insert되도록 PK 오름차순 정렬 후 저장.
                // 이 정렬은 parent_id < 자기_id라는 현재 데이터의 불변식에 의존한다. 이 조건이 깨지는 데이터가
                // 추가되면 FK 위반으로 실패하니, 그 경우 위상정렬(topological sort)로 교체가 필요하다.
                entities.sort(Comparator.comparing(RuleExpressionNode::getExpressionId));
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), ruleExpressionNodeRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadRuleActions() throws IOException {
        String table = "rule_action";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("rule_action_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (actionRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, actionRepository.count());
            } else {
                List<Action> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Action.builder()
                            .actionId(ExcelUtil.getLong(row, 0))
                            .rule(graduationRuleRepository.getReferenceById(ExcelUtil.getLong(row, 1)))
                            .actionType(ExcelUtil.getString(row, 2))
                            .target(ExcelUtil.getString(row, 3))
                            .value(ExcelUtil.getDouble(row, 4))
                            .priority(ExcelUtil.getInteger(row, 5))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), actionRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadGraduationRequirements() throws IOException {
        String table = "graduation_requirements";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("graduation_requirements_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (graduationRequirementRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, graduationRequirementRepository.count());
            } else {
                List<GraduationRequirement> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(GraduationRequirement.builder()
                            .gradRequirementId(ExcelUtil.getLong(row, 0))
                            .requirement(requirementRepository.getReferenceById(ExcelUtil.getLong(row, 1)))
                            .curriculumVersion(curriculumVersionRepository.getReferenceById(ExcelUtil.getLong(row, 2)))
                            .requirementType(RequirementValueType.valueOf(ExcelUtil.getString(row, 3)))
                            .requiredValue(ExcelUtil.getInteger(row, 4))
                            .unit(RequirementUnit.valueOf(ExcelUtil.getString(row, 5)))
                            .priority(ExcelUtil.getInteger(row, 6))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), graduationRequirementRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCourseRequirementMappings() throws IOException {
        String table = "course_requirement_mapping";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("course_requirement_mapping_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (courseRequirementMappingRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, courseRequirementMappingRepository.count());
            } else {
                List<CourseRequirementMapping> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(CourseRequirementMapping.builder()
                            .mappingId(ExcelUtil.getLong(row, 0))
                            .course(courseRepository.getReferenceById(ExcelUtil.getLong(row, 1)))
                            .curriculumVersion(curriculumVersionRepository.getReferenceById(ExcelUtil.getLong(row, 2)))
                            .requirement(requirementRepository.getReferenceById(ExcelUtil.getLong(row, 3)))
                            .recognizedCredit(ExcelUtil.getBigDecimal(row, 4))
                            .priority(ExcelUtil.getInteger(row, 5))
                            .duplicationPolicy(MappingDuplicationPolicy.valueOf(ExcelUtil.getString(row, 6)))
                            // rule_id는 정수 PK로 변환하지 않고 원본 문자열 그대로 저장 (예: "R001" — Validation_Report_v1.1.md §3).
                            .ruleId(ExcelUtil.getString(row, 7))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), courseRequirementMappingRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadDuplicateRules() throws IOException {
        String table = "duplicate_rules";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("duplicate_rules_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (duplicationRuleRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, duplicationRuleRepository.count());
            } else {
                List<DuplicationRule> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(DuplicationRule.builder()
                            .duplicateId(ExcelUtil.getLong(row, 0))
                            .groupId(ExcelUtil.getInteger(row, 1))
                            .curriculumVersion(curriculumVersionRepository.getReferenceById(ExcelUtil.getLong(row, 2)))
                            .course(courseRepository.getReferenceById(ExcelUtil.getLong(row, 3)))
                            .requirement(requirementRepository.getReferenceById(ExcelUtil.getLong(row, 4)))
                            .recognizedCredit(ExcelUtil.getBigDecimal(row, 5))
                            .policy(DuplicatePolicy.valueOf(ExcelUtil.getString(row, 6)))
                            .priority(ExcelUtil.getInteger(row, 7))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), duplicationRuleRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadPrerequisites() throws IOException {
        String table = "prerequisites";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("prerequisites_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (prerequisiteRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, prerequisiteRepository.count());
            } else {
                List<Prerequisite> entities = new ArrayList<>();
                for (Row row : rows) {
                    entities.add(Prerequisite.builder()
                            .prerequisiteId(ExcelUtil.getLong(row, 0))
                            .course(courseRepository.getReferenceById(ExcelUtil.getLong(row, 1)))
                            .prerequisiteCourse(courseRepository.getReferenceById(ExcelUtil.getLong(row, 2)))
                            .relationType(PrerequisiteRelationType.valueOf(ExcelUtil.getString(row, 3)))
                            .curriculumVersion(curriculumVersionRepository.getReferenceById(ExcelUtil.getLong(row, 4)))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), prerequisiteRepository.count());
        } finally {
            wb[0].close();
        }
    }

    private void loadCourseTags() throws IOException {
        String table = "course_tags";
        Workbook[] wb = new Workbook[1];
        Sheet sheet = openSheet("course_tags_v1.1.xlsx", wb);
        try {
            List<Row> rows = ExcelUtil.dataRows(sheet);
            if (courseTagRepository.count() > 0) {
                log.info("[SKIP] {}: already has {} rows", table, courseTagRepository.count());
            } else {
                List<CourseTag> entities = new ArrayList<>();
                for (Row row : rows) {
                    Long courseId = ExcelUtil.getLong(row, 0);
                    Long tagId = ExcelUtil.getLong(row, 1);
                    entities.add(CourseTag.builder()
                            .id(new CourseTagId(courseId, tagId))
                            .course(courseRepository.getReferenceById(courseId))
                            .tag(tagRepository.getReferenceById(tagId))
                            .build());
                }
                persistAll(entities);
                log.info("[INSERT] {}: inserted {} rows", table, entities.size());
            }
            validateCount(table, rows.size(), courseTagRepository.count());
        } finally {
            wb[0].close();
        }
    }
}
