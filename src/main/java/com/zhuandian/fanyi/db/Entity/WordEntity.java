package com.zhuandian.fanyi.db.Entity;

/**
 * Created by 谢栋 on 2016/10/29.
 */
public class WordEntity {
    private String chinese;
    private String english;

    public WordEntity(String chinese, String english) {
        this.chinese = chinese;
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
