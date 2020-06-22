package com.usian.service;

import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.LocalMessageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LocalMessageImpl implements LocalMessageService {

    @Autowired
    private LocalMessageMapper localMessageMapper;

    @Override
    public List<LocalMessage> selectlocalMessageByStatus() {
        LocalMessageExample localMessageExample = new LocalMessageExample();
        LocalMessageExample.Criteria criteria = localMessageExample.createCriteria();
        criteria.andStateEqualTo(0);
        List<LocalMessage> localMessages = localMessageMapper.selectByExample(localMessageExample);
        return localMessages;
    }
}
