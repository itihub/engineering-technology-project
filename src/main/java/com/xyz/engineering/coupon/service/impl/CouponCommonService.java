package com.xyz.engineering.coupon.service.impl;

import com.xyz.engineering.coupon.annotation.Time;
import com.xyz.engineering.coupon.cache.CouponTemplateCacheHelper;
import com.xyz.engineering.coupon.cache.UserCouponCacheHelper;
import com.xyz.engineering.coupon.entity.CouponTemplate;
import com.xyz.engineering.coupon.entity.UserCoupon;
import com.xyz.engineering.coupon.service.ICouponCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 优惠券通用功能接口实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCommonService implements ICouponCommonService {

    private final CouponTemplateCacheHelper couponTemplateCacheHelper;

    private final UserCouponCacheHelper userCouponCacheHelper;

    /**
     * 保证数据一致性
     * 预防小概率事件：
     *  1.给缓存数据加上过期时间 date
     *  2.特别重要的数据，可以使用异步线程、定时JOB主动对比缓存和数据库中的数据是否一致
     * @param userCoupon
     */
    @Time
    @Override
    public void modifyUserCoupon(UserCoupon userCoupon) {
        // 1.更新数据库

        // 2.删除缓存
        userCouponCacheHelper.delete(userCoupon.getUserId(),
                Collections.singleton(userCoupon.getCouponId()));

        log.info("modify user coupon: [user-id={}] [couponId={}]",
                userCoupon.getUserId(), userCoupon.getCouponId());
    }

    @Override
    public List<CouponTemplate> queryCouponTemplate(List<Long> templateIds) {
        if(CollectionUtils.isEmpty(templateIds)) {
            return Collections.emptyList();
        }

        Map<Long, Optional<CouponTemplate>> templateId2Template =
                couponTemplateCacheHelper.getCouponTemplateByTemplateId(templateIds);

        // 过滤出缺失的templateId，也就是DB中查询不到的template id
        List<CouponTemplate> fakeTemplateIds = new ArrayList<>(templateIds.size());
        List<CouponTemplate> result = new ArrayList<>(templateIds.size());

        templateId2Template.forEach((k, v) -> {
            if (v.isPresent()) {
                result.add(v.get());
            } else {
                fakeTemplateIds.add(CouponTemplate.builder().templateId(k).build());
            }
        });

        // 缓冲中手动插入 fake coupon template，避免缓存穿透
        if (CollectionUtils.isNotEmpty(fakeTemplateIds)) {
            fakeTemplateIds.forEach((couponTemplateCacheHelper::putCouponTemplateToCache));
        }

        // 将缺失的templateId填充到返回结果中
        result.addAll(fakeTemplateIds);

        return result;
    }

    @Override
    public List<UserCoupon> queryUserCoupon(Long userId, List<Long> couponIds) {
        if(Objects.isNull(userId) ||  CollectionUtils.isEmpty(couponIds)) {
            return Collections.emptyList();
        }

        List<Long> missingCouponIds = new ArrayList<>(couponIds.size());
        List<UserCoupon> result = new ArrayList<>(couponIds.size());

        Map<Long, UserCoupon> couponId2Coupon =
                userCouponCacheHelper.getUserCouponFromCache(userId, new HashSet<>(couponIds));

        if (MapUtils.isEmpty(couponId2Coupon)) {
            missingCouponIds.addAll(couponIds);
        } else {
            result.addAll(couponId2Coupon.values());
            // 从cache中获取到的数据比请求少
            if (couponId2Coupon.size() < couponIds.size()) {
                // request coupon ids - cache coupon ids
                missingCouponIds.addAll(CollectionUtils.subtract(couponIds, couponId2Coupon.keySet()));
            }
        }

        // 如果 missing coupon ids 不为空，则存在两种可能：db中有， db中没有
        if (CollectionUtils.isNotEmpty(missingCouponIds)) {
            // TODO 方式一：从数据表中查询装填到缓存中，如果数据表中不存在则以空值的方式装填到缓存中 避免缓存穿透
            //      方式二：监控用户的请求针对非法的请求进行限流或拦截、抛出异常 等等 规避缓存穿透
            // 根据业务需求选择
        }

        // TODO 将 missing coupon ids 的数据包装到 返回结果中 才完整

        return result;
    }
}
