package com.itheima.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class HotSetMeal implements Serializable {
    private String name;
    private Long setmeal_count;
    private BigDecimal proportion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSetmeal_count() {
        return setmeal_count;
    }

    public void setSetmeal_count(Long setmeal_count) {
        this.setmeal_count = setmeal_count;
    }

    public BigDecimal getProportion() {
        return proportion;
    }

    public void setProportion(BigDecimal proportion) {
        this.proportion = proportion;
    }
}
