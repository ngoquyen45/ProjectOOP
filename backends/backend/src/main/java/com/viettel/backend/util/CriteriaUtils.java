package com.viettel.backend.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.query.Criteria;

import com.viettel.backend.util.entity.SimpleDate.Period;

public class CriteriaUtils {

    public static Criteria andOperator(Criteria... criterias) {
        List<Criteria> criteriaValids = new LinkedList<Criteria>();

        if (criterias != null && criterias.length > 0) {
            for (Criteria c : criterias) {
                if (c != null) {
                    criteriaValids.add(c);
                }
            }
        }

        if (criteriaValids.isEmpty()) {
            return null;
        } else if (criteriaValids.size() == 1) {
            return criteriaValids.get(0);
        } else {
            criterias = new Criteria[criteriaValids.size()];
            criterias = criteriaValids.toArray(criterias);
            return new Criteria().andOperator(criterias);
        }
    }

    public static Criteria orOperator(Criteria... criterias) {
        List<Criteria> criteriaValids = new LinkedList<Criteria>();

        if (criterias != null && criterias.length > 0) {
            for (Criteria c : criterias) {
                if (c != null) {
                    criteriaValids.add(c);
                }
            }
        }

        if (criteriaValids.isEmpty()) {
            return null;
        } else if (criteriaValids.size() == 1) {
            return criteriaValids.get(0);
        } else {
            criterias = new Criteria[criteriaValids.size()];
            criterias = criteriaValids.toArray(criterias);
            return new Criteria().orOperator(criterias);
        }
    }

    public static Criteria getSearchLikeCriteria(String field, String search) {
        Criteria searchCriteria = null;
        if (!StringUtils.isNullOrEmpty(search)) {
            String searchText = StringUtils.getSearchableString(search);
            searchCriteria = Criteria.where(field).regex(searchText, "i");
        }

        return searchCriteria;
    }

    public static Criteria getSearchInsensitiveCriteria(String field, String search) {
        Criteria searchCriteria = null;
        if (!StringUtils.isNullOrEmpty(search)) {
            Pattern pattern = Pattern.compile("^" + Pattern.quote(search) + "$", Pattern.CASE_INSENSITIVE);
            searchCriteria = Criteria.where(field).regex(pattern);
        }

        return searchCriteria;
    }

    public static final Criteria getPeriodCriteria(String field, Period period) {
        Criteria criteria = Criteria.where(field);
        if (period.getFromDate() != null) {
            criteria.gte(period.getFromDate().getValue());
        }
        if (period.getToDate() != null) {
            criteria.lt(period.getToDate().getValue());
        }
        return criteria;
    }

}
