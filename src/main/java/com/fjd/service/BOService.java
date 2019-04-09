//package com.fjd.service;
//
//import com.fjd.exception.BusinessException;
//import com.fjd.model.User;
//import com.fjd.query.QueryUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.dozer.MappingProcessor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import javax.persistence.OptimisticLockException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public class BOService {
//    private static final Logger LOGGER = LoggerFactory.getLogger(BOService.class);
//    public Long countBOs(String filter) {
//        MappingProcessor mappingProcessor = (MappingProcessor) mapper.getMappingProcessor();
//        Map<String, Object> paramMap = new HashMap<>();
//        try {
//            String queryStmt = QueryUtils.buildBOQL(paramMap, boFacade, mappingProcessor, roClass, boClass, filter,
//                    null, true);
//            BOQLQuery<Long> query = boFacade.createQuery(queryStmt, Long.class);
//            for (Map.Entry<String, Object> e : paramMap.entrySet()) {
//                query.setParameter(e.getKey(), e.getValue());
//            }
//            List<Long> res = query.getResultList();
//            return CollectionUtils.isEmpty(res) ? 0 : (Long) res.get(0);
//        } catch (BusinessException be) {
//            LOGGER.info("Failed to parse or generate BOQL for filter: {} due to {}! Check log for detail.", filter, be);
//            if (!BOFrwNSErrorCode.BOQL_CREATE_QUERY_ERROR.equals(be.getErrorCode())) {
//                throw be;
//            } else {
//                throw new BusinessException(APIFrwErrorCode.API_QUERY_INVALID_GENERIC);
//            }
//        } catch (Exception e) {
//            LOGGER.info("Failed to parse or generate BOQL for filter: {} due to {}! Check log for detail.", filter, e);
//            throw new BusinessException(APIFrwErrorCode.API_QUERY_INVALID_GENERIC, e);
//        }
//    }
//
//    /**
//     * @param ro
//     * @return
//     */
//    public long createBO(RO ro) {
//        LOGGER.info("Create BO {}.", boClass);
//
//        prepareMapping(DozerMappingDirection.RO2BO, null, null);
//
//        this.validateOnCreate(ro);
//        BO bo = boFacade.createBusinessObject(boClass);
//        this.convert2BO(ro, bo);
//        bo.create();
//
//        return bo.getId();
//    }
//
//    /**
//     * @param id
//     * @param ro
//     */
//    public void updateBO(long id, RO ro) {
//        LOGGER.info("Update BO {} id {}.", boClass, id);
//
//        persistUpdates();
//        prepareMapping(DozerMappingDirection.RO2BO, null, null);
//
//        BO bo = this.getBO(id);
//        this.validateOnUpdate(ro, bo);
//        this.convert2BO(ro, bo);
//        bo.update();
//    }
//    public void updateBO(long id, BO ro) {
//        Optional<User> oldUserOpt = userRepository.findById(id);
//        User oldUser = oldUserOpt.get();
//        oldUser.setName(user.getName());
//        oldUser.setEmail(user.getEmail());
//        if (user.getVersion() < oldUser.getVersion()) {
//            throw new OptimisticLockException("this record has been updated by others");
//        }
//        userRepository.save(oldUser);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    /**
//     * @param id
//     */
//    public void deleteBO(long id) {
//        LOGGER.info("Delete BO {} id {}.", boClass, id);
//        BO bo = this.getBO(id);
//
//        bo.delete();
//    }
//}
