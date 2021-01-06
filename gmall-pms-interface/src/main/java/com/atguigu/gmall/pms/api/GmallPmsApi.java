package com.atguigu.gamll.pms.api;

import com.atguigu.gamll.pms.entity.SpuEntity;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GmallPmsApi {
    @PostMapping("pms/spu/page")
    public ResponseVo<List<SpuEntity>> querySpusByPage(
            @RequestBody PageParamVo pageParamVo
    );
}
