package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.mapper.MemberMapper;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Override
    public Member findByTelephone(String telephone) throws Exception {
        return memberMapper.findByTelephone(telephone);
    }

    @Override
    public void rigister(Member member) throws Exception {
        //在注册前为了安全考虑将密码加密
        String password = member.getPassword();
        if (password!=null){
            member.setPassword(MD5Utils.md5(password));
        }
        memberMapper.add(member);
    }

    @Override
    public List<Integer> findCountByMonth(List<String> monthList) throws Exception {
        List<Integer> memberCountList = new ArrayList<>();
        //遍历集合，调用mapper中的befordate方法
        for (String month : monthList) {
            month =month+".31";
            Integer memberCount = memberMapper.findMemberCountBeforeDate(month);
            memberCountList.add(memberCount);
        }
        return memberCountList;
    }
}
