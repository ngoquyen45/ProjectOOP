package com.viettel.backend.service.report.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.SurveyAnswer;
import com.viettel.backend.domain.embed.SurveyQuestion;
import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.SurveyResultDto;
import com.viettel.backend.dto.report.SurveyResultDto.SurveyOptionResultDto;
import com.viettel.backend.dto.report.SurveyResultDto.SurveyQuestionResultDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.SurveyAnswerRepository;
import com.viettel.backend.repository.SurveyRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.report.SurveyReportService;

@RolePermission(value = { Role.ADMIN, Role.OBSERVER, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class SurveyReportServiceImpl extends AbstractService implements SurveyReportService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;

    @Override
    public ListDto<SurveyListDto> getSurveys(UserLogin userLogin, String search, Pageable pageable) {
        Sort sort = new Sort(Direction.DESC, Survey.COLUMNNAME_START_DATE_VALUE);
        List<Survey> surveys = surveyRepository.getSurveysStarted(userLogin.getClientId(), search, pageable, sort);
        if (CollectionUtils.isEmpty(surveys) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<SurveyListDto> dtos = new ArrayList<SurveyListDto>(surveys.size());
        for (Survey survey : surveys) {
            dtos.add(new SurveyListDto(survey));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || pageable.getPageSize() == size) {
                size = surveyRepository.countSurveysStarted(userLogin.getClientId(), search);
            }
        }

        return new ListDto<SurveyListDto>(dtos, size);
    }

    @Override
    public SurveyResultDto getSurveyReport(UserLogin userLogin, String surveyId) {
        Survey survey = getMandatoryPO(userLogin, surveyId, surveyRepository);

        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<SurveyAnswer> answers = surveyAnswerRepository.getSurveyAnswerByDistributors(userLogin.getClientId(),
                survey.getId(), distributorIds);

        HashMap<ObjectId, Integer> resultByOptionId = new HashMap<ObjectId, Integer>();
        if (answers != null) {
            for (SurveyAnswer answer : answers) {
                if (answer.getOptions() != null) {
                    for (ObjectId optionId : answer.getOptions()) {
                        Integer result = resultByOptionId.get(optionId);
                        if (result == null) {
                            result = 0;
                        }
                        result = result + 1;
                        resultByOptionId.put(optionId, result);
                    }
                }
            }
        }

        List<SurveyQuestionResultDto> questionResults = new LinkedList<>();
        if (survey.getQuestions() != null) {
            for (SurveyQuestion question : survey.getQuestions()) {
                
                if (question.getOptions() != null) {
                    List<SurveyOptionResultDto> optionResults = new LinkedList<>();
                    for (CategoryEmbed option : question.getOptions()) {
                        Integer result = resultByOptionId.get(option.getId());
                        result = result == null ? 0 : result;
                        
                        SurveyOptionResultDto optionResultDto = new SurveyOptionResultDto(option, result);

                        optionResults.add(optionResultDto);
                    }
                    
                    questionResults.add(new SurveyQuestionResultDto(question, optionResults));
                }
                
            }
        }
        
        return new SurveyResultDto(survey, questionResults);
    }

    @Override
    public byte[] exportSurveyReport(UserLogin userLogin, String surveyId, String lang) {
        lang = getLang(lang);
        
        Survey survey = getMandatoryPO(userLogin, surveyId, surveyRepository);

        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        Set<ObjectId> distributorIds = getIdSet(distributors);

        List<SurveyAnswer> answers = surveyAnswerRepository.getSurveyAnswerByDistributors(userLogin.getClientId(),
                survey.getId(), distributorIds);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(translate(lang, "survey"));

        if (answers == null) {
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue(translate(lang, "empty"));
        } else {
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue(translate(lang, "time"));
            row.createCell(1).setCellValue(translate(lang, "customer"));
            row.createCell(2).setCellValue(translate(lang, "salesman"));
            if (survey.getQuestions() != null) {
                int index = 3;

                for (SurveyQuestion question : survey.getQuestions()) {
                    row.createCell(index).setCellValue(question.getName());
                    index++;
                }
            }

            int answerIndex = 1;
            for (SurveyAnswer answer : answers) {
                row = sheet.createRow(answerIndex);
                row.createCell(0).setCellValue(answer.getVisit().getEndTime().format("dd/MM/yyyy HH:mm"));
                row.createCell(1).setCellValue(answer.getVisit().getCustomer().getName());
                row.createCell(2).setCellValue(answer.getVisit().getSalesman().getFullname());

                if (survey.getQuestions() != null) {
                    int index = 3;

                    for (SurveyQuestion question : survey.getQuestions()) {
                        StringBuilder optionsDisplay = new StringBuilder();
                        boolean isFirst = true;
                        if (question.getOptions() != null) {
                            for (CategoryEmbed option : question.getOptions()) {
                                if (answer.getOptions() != null && answer.getOptions().contains(option.getId())) {
                                    if (!isFirst) {
                                        optionsDisplay.append(", ");
                                    }
                                    isFirst = false;

                                    optionsDisplay.append(option.getName());
                                }
                            }
                        }

                        row.createCell(index).setCellValue(optionsDisplay.toString());

                        index++;
                    }
                }

                answerIndex++;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
