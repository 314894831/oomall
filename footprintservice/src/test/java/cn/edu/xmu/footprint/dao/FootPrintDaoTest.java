package cn.edu.xmu.footprint.dao;
import cn.edu.xmu.footprint.model.vo.FootPrintVo;
import cn.edu.xmu.footprint.FootPrintServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FootPrintServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class FootPrintDaoTest {

    @Autowired
    private FootPrintDao dao;

    @Test
    @Commit
    public void oo(){
        FootPrintVo vo = null;
        vo.setGoodsSpuId(123L);
dao.addFootPrintAndVo(1L,vo);

    }}
