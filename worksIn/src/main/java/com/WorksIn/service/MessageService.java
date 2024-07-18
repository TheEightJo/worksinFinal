package com.WorksIn.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MessageService {
    public String numberCheck(String to) throws CoolsmsException{
        String api_key = "NCSJ9TEJKLECDUTG";
        String api_secret = "JQJMQCMFY2T8NXLBEYYSP1F8NPZCZV07";

        Message coolsms = new Message(api_key, api_secret);
        Random rd = new Random();
        String numStr = "";
        for(int i = 0; i<6; i++){
            String ran = Integer.toString(rd.nextInt(10));
            numStr += ran;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", to);
        params.put("from", "01089726120");
        params.put("type", "sms");
        params.put("text", "WorksOut 휴대폰 인증 번호 [" + numStr + "]");

        coolsms.send(params);

        System.out.println("대상 : " + to);
        System.out.println("인증코드 : " + numStr);

        return numStr;
    }
}
