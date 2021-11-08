package com.lchli.lottery.priv;

import com.lchli.lottery.BaseResponse;
import com.lchli.lottery.user.UserModelMapper;
import com.lchli.lottery.user.model.UserResponse;
import com.lchli.lottery.user.repo.UserRepo;
import com.lchli.lottery.user.repo.entity.User;
import com.lchli.lottery.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sec/pri")
public class PriController {

    @Autowired
    ProtoRepo userRepo;


    @PostMapping(path = "/save", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public BaseResponse save(@RequestParam(value = "id", required = false) String id,
                                 @RequestParam(value = "type", required = true) String type,
                                 @RequestParam(value = "content", required = true) String content
    ) {

        BaseResponse response = new BaseResponse();
        response.status = BaseResponse.RESPCODE_FAIL;
        if(!type.equals(ProtoModel.USER)&&!type.equals(ProtoModel.PRI)){
            response.message="type not valid.";
            return response;
        }
        ProtoModel old=userRepo.findByType(type);
        if(old==null){
            old=new ProtoModel();
        }
        old.type = type;
        old.content = content;

        userRepo.save(old);

        response.status = BaseResponse.RESPCODE_SUCCESS;

        return response;

    }


    @GetMapping(path = "/query", produces = {MediaType.APPLICATION_JSON_VALUE})
    public PriReponse login(@RequestParam(value = "type", required = true) String type
    ) {
        PriReponse response = new PriReponse();
        response.status = BaseResponse.RESPCODE_FAIL;

        if (!type.equals(ProtoModel.PRI)&&!type.equals(ProtoModel.USER)) {
            response.message = "type参数不合法";
            return response;
        }


        ProtoModel data = userRepo.findByType(type);

        response.status = BaseResponse.RESPCODE_SUCCESS;
        response.data = data;

        return response;

    }

}
