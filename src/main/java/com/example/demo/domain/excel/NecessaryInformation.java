package com.example.demo.domain.excel;

import java.util.*;

import static com.example.demo.common.SchoolCommonValue.*;

//	 implements Operation
public enum NecessaryInformation {
    STUDENT_UPDATE {
        @Override
        public Map<String, Integer> getNecessaryMap() {
            Map<String, Integer> map = new LinkedHashMap<String, Integer>();
            map.put(USER_ID, -1);
            map.put(USER_NAME, -1);
            map.put(FIRST_NAME, -1);
            map.put(LAST_NAME, -1);
            return map;
        }

    },
    GRADE_CLASS_ONLY {
        @Override
        public Map<String, Integer> getNecessaryMap() {
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put(GRADE, -1);
            map.put(CLASS_NAME, -1);
            map.put(YEAR, -1);
            return map;
        }
    },
    STUDENT_GRADE_CLASS_UPDATE {
        @Override
        public Map<String, Integer> getNecessaryMap() {
            Map<String, Integer> map = new LinkedHashMap<String, Integer>();
            map.put(USER_ID, -1);
            map.put(USER_NAME, -1);
            map.put(FIRST_NAME, -1);
            map.put(LAST_NAME, -1);
            map.put(GRADE, -1);
            map.put(CLASS_NAME, -1);
            map.put(YEAR, -1);
            return map;
        }
    },
    STUDENT_GRADE_CLASS_INSERT {
        @Override
        public Map<String, Integer> getNecessaryMap() {
            Map<String, Integer> map = new LinkedHashMap<String, Integer>();
            map.put(USER_NAME, -1); // User_Nameは初期値として作成されていることを想定
            map.put(FIRST_NAME, -1);
            map.put(LAST_NAME, -1);
            map.put(GRADE, -1);
            map.put(CLASS_NAME, -1);
            map.put(YEAR, -1);
            return map;
        }
    },
    STUDENT_TEST {
        @Override
        public Map<String, Integer> getNecessaryMap() {
            Map<String, Integer> map = new LinkedHashMap<String, Integer>();
            map.put(USER_ID, -1);
            map.put(USER_NAME, -1);
            map.put(FIRST_NAME, -1);
            map.put(LAST_NAME, -1);
            map.put(CLASS_NAME, -1);
            map.put(SEASON_NAME, -1);
            map.put(SUBJECT_NAME, -1);
            map.put(YEAR, -1);
            map.put(GRADE, -1);
            map.put(POINT, -1);
            return map;
        }
    };

    public abstract Map<String, Integer> getNecessaryMap();

    public List<Map<String, String>> returnModelMap() {
        return new ArrayList<Map<String, String>>();
    }

}
