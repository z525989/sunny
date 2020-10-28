package com.zjh.sunny.websocket.mapping;


import com.zjh.sunny.core.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 绑定netty请求controller
 */
@Component
public class WebSocketMappingBindHandler {

    private final static Logger logger = LoggerFactory.getLogger(WebSocketMappingBindHandler.class);

    private Map<Integer, Method> methodMap = new HashMap<>();

    public void bind(String scanPackage) {
        try {
            //根据包名获取类列表
            List<Class<?>> clazzList = ClassUtil.getClassList(scanPackage, true);
            clazzList.forEach(this::scanAndBind);
        } catch (Exception e) {
            logger.error("BindNettyController Error", e);
        } finally {
            logger.info("protocolCodeMapper Size：" + methodMap.size());
        }
    }

    private void scanAndBind(Class<?> clazz) {
        //过滤非NettyController类
        WebSocketController nettyController = clazz.getAnnotation(WebSocketController.class);
        if (nettyController == null) {
            return;
        }

        //遍历NettyRequestMapper方法
        Method[] methodArr = clazz.getMethods();

        for (Method method : methodArr) {
            if (!method.isAnnotationPresent(WebSocketMapping.class)) {
                continue;
            }

            //获取类注解
            WebSocketMapping requestMapper = method.getAnnotation(WebSocketMapping.class);
            if (requestMapper == null) {
                continue;
            }

            int protocolCode = requestMapper.protocolCode();

            if (protocolCode == 0 || methodMap.containsKey(protocolCode)) {
                throw new RuntimeException("protocolCode is exist");
            }

            methodMap.put(protocolCode, method);
        }
    }

    public Method getMethod(int protocolCode) {
        return methodMap.get(protocolCode);
    }
}
