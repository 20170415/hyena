/*
 *  Copyright (C) 2019 Alpha Jiang. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.github.alphajiang.hyena.aop;

import io.github.alphajiang.hyena.HyenaConstants;
import io.github.alphajiang.hyena.biz.idempotent.HyenaIdempotent;
import io.github.alphajiang.hyena.model.base.BaseResponse;
import io.github.alphajiang.hyena.model.param.PointOpParam;
import io.github.alphajiang.hyena.utils.JsonUtils;
import io.github.alphajiang.hyena.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@Aspect
public class IdempotentAround {
    private final Logger logger = LoggerFactory.getLogger(IdempotentAround.class);

    @Autowired
    private HyenaIdempotent hyenaIdempotent;


    @Around("@annotation(Idempotent)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();

        Idempotent shelter = method.getAnnotation(Idempotent.class);
        String name = shelter.name();
        Object[] args = point.getArgs();
        HttpServletRequest request = (HttpServletRequest) args[0];
        PointOpParam param = (PointOpParam) args[1];
        BaseResponse res;

        // String seq = request.getParameter(HyenaConstants.REQ_IDEMPOTENT_SEQ_KEY);
        String seq = param.getSeq();
        request.setAttribute(HyenaConstants.REQ_IDEMPOTENT_SEQ_KEY, seq);

        res = this.preProceed(name, param, method);

        if (res != null) {
            return res;
        }

        res = (BaseResponse) point.proceed(point.getArgs());

        this.postProceed(name, param, res);
        return res;
    }

    private BaseResponse preProceed(String name, PointOpParam param, Method method)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        BaseResponse res = null;
        if (StringUtils.isBlank(param.getSeq())) {
            return res;
        }
        String key = getKey(name, param);
        String resMsg = this.hyenaIdempotent.getByKey(key);
        if (StringUtils.isNotBlank(resMsg)) {    // cache match
            res = (BaseResponse) JsonUtils.fromJson(resMsg, method.getReturnType());
            logger.info("idempotent cache matched. res = {}", JsonUtils.toJsonString(res));
            return res;
        }

        if (!this.hyenaIdempotent.lock(key)) {
            res = (BaseResponse) method.getReturnType().getDeclaredConstructor().newInstance();
            res.setStatus(HyenaConstants.RES_CODE_DUPLICATE_IDEMPOTENT);
            res.setError("请勿重复提交");

        }
        return res;
    }

    private void postProceed(String name, PointOpParam param, BaseResponse res) {
        if (StringUtils.isNotBlank(param.getSeq())) {
            String key = getKey(name, param);
            res.setSeq(param.getSeq());
            this.hyenaIdempotent.setByKey(key, res);

            this.hyenaIdempotent.unlock(key);
        }
    }

    private String getKey(String name, PointOpParam param) {
        StringBuilder buf = new StringBuilder();
        if (StringUtils.isNotBlank(name)) {
            buf.append(name).append("-");
        }
        if (StringUtils.isNotBlank(param.getType())) {
            buf.append(param.getType()).append("-");
        }
        buf.append(param.getSeq());
        return buf.toString();
    }

}
