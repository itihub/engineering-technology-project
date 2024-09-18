package com.xyz.engineering.coupon.writeoff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 优惠券核销工程类
 */
@Slf4j
@Component
public class CouponWriteOffFactory implements BeanPostProcessor {

    private static final Map<CouponTypeEnum, ICouponWriteOffService> serviceMap
            = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {

        // 只需要关注处理 ICouponWriteOffService 的实现类
        if (bean instanceof ICouponWriteOffService service) {
            serviceMap.put(service.type(), service);
        }

        return bean;
    }

    public ICouponWriteOffService getWriteOffService(CouponTypeEnum typeEnum) {
        return serviceMap.get(typeEnum);
    }

    // TODO 问题
    // 1.优惠券可以叠加，同时核销多个优惠券；如何实现？
    // 2.优惠券的适用场景不一定相同，多个优惠券之间可能是不能共用的（同品类，不同品类，存在互斥的关系）；如何实现？

    // tips：所有的功能都是业务逻辑控制的，而业务逻辑是通过外部的输入得到的 -> CouponTemplate config
}
