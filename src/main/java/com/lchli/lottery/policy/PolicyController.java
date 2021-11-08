package com.lchli.lottery.policy;

import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.priv.PriReponse;
import com.lchli.lottery.priv.ProtoModel;
import com.lchli.lottery.priv.ProtoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sec/policy")
public class PolicyController {

    @Autowired
    PolicyRepo userRepo;


    @PostMapping(path = "/save", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse save(@RequestParam(value = "id", required = false) String id,
                             @RequestParam(value = "type", required = true) String type,
                             @RequestParam(value = "sw", required = true) int sw
    ) {

        BaseResponse response = new BaseResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        PolicyModel old = userRepo.findByType(type);
        if (old == null) {
            old = new PolicyModel();
        }
        old.asw = sw;

        userRepo.save(old);

        response.status = BaseResponse.RESPCODE_SUCCESS;

        return response;

    }


    @GetMapping(path = "/query", produces = {MediaType.APPLICATION_JSON_VALUE})
    public PolicyResponse login(@RequestParam(value = "type", required = true) String type
    ) {
        PolicyResponse response = new PolicyResponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (!type.equals(PolicyModel.AD_POLI)) {
            response.message = "type参数不合法";
            return response;
        }


        PolicyModel data = userRepo.findByType(type);

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = data;

        return response;

    }

}
