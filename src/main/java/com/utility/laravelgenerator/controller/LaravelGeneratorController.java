package com.utility.laravelgenerator.controller;

import com.utility.laravelgenerator.pojo.BaseResponse;
import com.utility.laravelgenerator.service.impl.LaravelGeneratorServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LaravelGeneratorController {

    @Autowired
    private LaravelGeneratorServiceImpl laravelGeneratorService;
    
//    @GetMapping("/heartbeat")
//    public BaseResponse heartbeat() {
//        return BaseResponse.SUCCESS;
//    }

    @ApiOperation(value="根据表名列表生成laravel框架中的controller,model,route")
    @GetMapping("/generate")
    public BaseResponse generate(@RequestParam String tablenames) {
        laravelGeneratorService.generate(tablenames);
        return BaseResponse.SUCCESS;
    }

    @ApiOperation(value="返回数据库中的所有表名")
    @GetMapping("/tablenames")
    public String queryTableNames() {
        return laravelGeneratorService.queryTableNames();
    }

    @ApiOperation(value="执行select查询语句,检查是否会有报错")
    @GetMapping("/testquery")
    public BaseResponse testQuery(@RequestParam String querysql) {
        laravelGeneratorService.testQuery(querysql);
        return BaseResponse.SUCCESS;
    }

    @ApiOperation(value="检查数据库里的表和字段命名是否和mysql关键字冲突等")
    @GetMapping("/check")
    public String check() {
        return laravelGeneratorService.check();
    }
}
