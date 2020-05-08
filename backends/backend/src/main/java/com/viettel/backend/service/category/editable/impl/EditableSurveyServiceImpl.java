package com.viettel.backend.service.category.editable.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.SurveyQuestion;
import com.viettel.backend.dto.category.SurveyCreateDto;
import com.viettel.backend.dto.category.SurveyCreateDto.SurveyQuestionCreateDto;
import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.SurveyRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableSurveyService;
import com.viettel.backend.util.entity.SimpleDate;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditableSurveyServiceImpl extends
        AbstractCategoryEditableService<Survey, SurveyListDto, SurveyDto, SurveyCreateDto> implements
        EditableSurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Override
    public CategoryRepository<Survey> getRepository() {
        return surveyRepository;
    }
    
    @Override
    protected void beforeSetActive(UserLogin userLogin, Survey domain, boolean active) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Sort getSort() {
        return new Sort(Direction.DESC, Survey.COLUMNNAME_END_DATE_VALUE);
    }

    @Override
    public Survey createDomain(UserLogin userLogin, SurveyCreateDto createdto) {
        Survey domain = new Survey();
        initPOWhenCreate(Survey.class, userLogin, domain);
        domain.setDraft(true);

        updateDomain(userLogin, domain, createdto);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Survey domain, SurveyCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");

        checkMandatoryParams(createdto.getStartDate(), createdto.getEndDate(), createdto.getQuestions());

        SimpleDate startDate = getMandatoryIsoDate(createdto.getStartDate());
        SimpleDate endDate = getMandatoryIsoDate(createdto.getEndDate());
        BusinessAssert.isTrue(startDate.compareTo(endDate) <= 0, "startDate > endDate");

        domain.setStartDate(startDate);
        domain.setEndDate(endDate);
        domain.setRequired(true);

        List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>(createdto.getQuestions().size());
        for (SurveyQuestionCreateDto questionDto : createdto.getQuestions()) {
            SurveyQuestion question = new SurveyQuestion();
            if (questionDto.getId() != null) {
                question.setId(getObjectId(questionDto.getId()));
            } else {
                question.setId(new ObjectId());
            }
            question.setName(questionDto.getName());
            question.setMultipleChoice(questionDto.isMultipleChoice());
            question.setRequired(questionDto.isRequired());

            List<CategoryCreateDto> optionDtos = questionDto.getOptions();
            if (optionDtos != null && !optionDtos.isEmpty()) {
                List<CategoryEmbed> options = new ArrayList<CategoryEmbed>(optionDtos.size());
                for (CategoryCreateDto optionDto : optionDtos) {
                    CategoryEmbed option = new CategoryEmbed();
                    if (optionDto.getId() != null) {
                        option.setId(getObjectId(optionDto.getId()));
                    } else {
                        option.setId(new ObjectId());
                    }
                    option.setName(optionDto.getName());

                    options.add(option);
                }

                question.setOptions(options);
            }
            questions.add(question);
        }

        domain.setQuestions(questions);
    }

    @Override
    public SurveyListDto createListSimpleDto(UserLogin userLogin, Survey domain) {
        return new SurveyListDto(domain);
    }

    @Override
    public SurveyDto createListDetailDto(UserLogin userLogin, Survey domain) {
        return new SurveyDto(domain);
    }

}
