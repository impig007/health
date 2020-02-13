package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;

public interface MemberService {
    Member findByTelephone(String telephone)throws Exception;

    void rigister(Member member)throws Exception;

    List<Integer> findCountByMonth(List<String> monthList)throws Exception;
}
